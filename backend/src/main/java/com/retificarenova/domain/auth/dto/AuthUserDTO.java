package com.retificarenova.domain.auth.dto;

import com.retificarenova.domain.shared.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserDTO {
    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
}

