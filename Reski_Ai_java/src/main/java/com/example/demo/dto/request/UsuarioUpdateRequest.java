package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioUpdateRequest(
        @NotBlank String nome,
        @Email @NotBlank String email,
        @NotBlank String senha
) {}
