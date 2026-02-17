package com.retificarenova.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthRequestDTO {

    @NotBlank
    private String code;

    private LocalDate birthDate;

    private String phone;

    private String fullName;
}

