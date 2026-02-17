package com.retificarenova.domain.auth.repository;

import com.retificarenova.domain.auth.entity.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProviderRepository extends JpaRepository<UserProvider, Long> {

    Optional<UserProvider> findByProviderAndProviderUserId(String provider, String providerUserId);

    Optional<UserProvider> findByUserIdAndProvider(Long userId, String provider);
}

