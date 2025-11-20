package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ObjetivoRequest(
        @NotBlank String cargo,
        @NotBlank String area,
        @NotBlank String descricao,
        @NotBlank String demanda
) {}
