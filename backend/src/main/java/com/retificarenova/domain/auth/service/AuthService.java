package com.retificarenova.domain.auth.service;

import com.retificarenova.domain.auth.dto.AuthUserDTO;
import com.retificarenova.domain.auth.dto.GoogleAuthRequestDTO;
import com.retificarenova.domain.auth.dto.GoogleTokenResponseDTO;
import com.retificarenova.domain.auth.dto.GoogleUserInfoDTO;
import com.retificarenova.domain.auth.dto.LoginRequestDTO;
import com.retificarenova.domain.auth.dto.LoginResponseDTO;
import com.retificarenova.domain.auth.dto.RefreshTokenRequestDTO;
import com.retificarenova.domain.auth.dto.RegisterRequestDTO;
import com.retificarenova.domain.auth.entity.RefreshToken;
import com.retificarenova.domain.auth.entity.User;
import com.retificarenova.domain.auth.entity.UserProvider;
import com.retificarenova.domain.auth.repository.RefreshTokenRepository;
import com.retificarenova.domain.auth.repository.UserRepository;
import com.retificarenova.domain.auth.repository.UserProviderRepository;
import com.retificarenova.domain.shared.enums.UserRole;
import com.retificarenova.exception.BusinessException;
import com.retificarenova.security.AuthUserDetails;
import com.retificarenova.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

@Service
public class AuthService {

    private static final String GOOGLE_REGISTRATION_ID = "google";
    private static final String GOOGLE_PROVIDER = "GOOGLE";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserProviderRepository userProviderRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RestTemplate restTemplate;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       UserProviderRepository userProviderRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       ClientRegistrationRepository clientRegistrationRepository,
                       RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userProviderRepository = userProviderRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use");
        }
        validateAdult(request.getBirthDate());

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .role(UserRole.USER)
                .build();

        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid credentials"));

        return buildAuthResponse(user);
    }

    @Transactional
    public LoginResponseDTO refresh(RefreshTokenRequestDTO request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new BusinessException("Refresh token expired or revoked");
        }

        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
        return buildAuthResponse(refreshToken.getUser());
    }

    @Transactional
    public void logout(RefreshTokenRequestDTO request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public LoginResponseDTO loginWithGoogle(GoogleAuthRequestDTO request) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(GOOGLE_REGISTRATION_ID);
        if (clientRegistration == null) {
            throw new BusinessException("Google OAuth client not configured");
        }

        GoogleTokenResponseDTO tokenResponse = exchangeCodeForToken(clientRegistration, request.getCode());
        GoogleUserInfoDTO userInfo = fetchGoogleUserInfo(clientRegistration, tokenResponse.getAccessToken());

        String providerUserId = userInfo.getSub();
        String email = userInfo.getEmail();
        Boolean emailVerified = userInfo.getEmailVerified();

        if (email == null || email.isBlank()) {
            throw new BusinessException("Google account does not provide email");
        }
        if (Boolean.FALSE.equals(emailVerified)) {
            throw new BusinessException("Google email is not verified");
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUserFromGoogle(request, userInfo));

        linkGoogleProvider(user, providerUserId);
        return buildAuthResponse(user);
    }

    private void validateAdult(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) {
            throw new BusinessException("User must be at least 18 years old");
        }
    }

    private GoogleTokenResponseDTO exchangeCodeForToken(ClientRegistration clientRegistration, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientRegistration.getClientId());
        body.add("client_secret", clientRegistration.getClientSecret());
        body.add("redirect_uri", clientRegistration.getRedirectUri());
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<GoogleTokenResponseDTO> response = restTemplate.postForEntity(
                clientRegistration.getProviderDetails().getTokenUri(),
                requestEntity,
                GoogleTokenResponseDTO.class
        );

        GoogleTokenResponseDTO tokenResponse = response.getBody();
        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new BusinessException("Unable to exchange Google code");
        }
        return tokenResponse;
    }

    private GoogleUserInfoDTO fetchGoogleUserInfo(ClientRegistration clientRegistration, String accessToken) {
        String userInfoUri = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();
        if (userInfoUri == null || userInfoUri.isBlank()) {
            throw new BusinessException("Google user info endpoint not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<GoogleUserInfoDTO> response = restTemplate.exchange(
                userInfoUri,
                org.springframework.http.HttpMethod.GET,
                requestEntity,
                GoogleUserInfoDTO.class
        );

        GoogleUserInfoDTO userInfo = response.getBody();
        if (userInfo == null || userInfo.getSub() == null) {
            throw new BusinessException("Unable to fetch Google user info");
        }
        return userInfo;
    }

    private User createUserFromGoogle(GoogleAuthRequestDTO request, GoogleUserInfoDTO userInfo) {
        if (request.getBirthDate() == null) {
            throw new BusinessException("Birth date required for first-time Google login");
        }
        validateAdult(request.getBirthDate());

        String fullName = request.getFullName();
        if (fullName == null || fullName.isBlank()) {
            fullName = userInfo.getName();
        }
        if (fullName == null || fullName.isBlank()) {
            fullName = userInfo.getEmail();
        }

        User user = User.builder()
                .fullName(fullName)
                .email(userInfo.getEmail())
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }

    private void linkGoogleProvider(User user, String providerUserId) {
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new BusinessException("Google account id is missing");
        }

        userProviderRepository.findByProviderAndProviderUserId(GOOGLE_PROVIDER, providerUserId)
                .ifPresent(existing -> {
                    if (!existing.getUser().getId().equals(user.getId())) {
                        throw new BusinessException("Google account already linked to another user");
                    }
                });

        userProviderRepository.findByUserIdAndProvider(user.getId(), GOOGLE_PROVIDER)
                .ifPresent(existing -> {
                    if (!existing.getProviderUserId().equals(providerUserId)) {
                        throw new BusinessException("User already linked to another Google account");
                    }
                });

        if (userProviderRepository.findByProviderAndProviderUserId(GOOGLE_PROVIDER, providerUserId).isEmpty()
                && userProviderRepository.findByUserIdAndProvider(user.getId(), GOOGLE_PROVIDER).isEmpty()) {
            UserProvider provider = UserProvider.builder()
                    .user(user)
                    .provider(GOOGLE_PROVIDER)
                    .providerUserId(providerUserId)
                    .build();
            userProviderRepository.save(provider);
        }
    }

    private LoginResponseDTO buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(new AuthUserDetails(user));
        RefreshToken refreshToken = createRefreshToken(user);

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtService.getAccessTokenExpirationMs())
                .tokenType("Bearer")
                .user(AuthUserDTO.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .build();
    }

    private RefreshToken createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpirationMs() / 1000))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
}
