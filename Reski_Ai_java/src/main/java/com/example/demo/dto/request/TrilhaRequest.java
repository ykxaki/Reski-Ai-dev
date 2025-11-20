package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TrilhaRequest(
        @NotBlank String status,
        @NotBlank String conteudo,
        @NotBlank String competencia
) {}
