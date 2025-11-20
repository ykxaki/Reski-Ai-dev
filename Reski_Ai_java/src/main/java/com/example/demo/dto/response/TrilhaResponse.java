package com.example.demo.dto.response;

import com.example.demo.model.Trilha;

public record TrilhaResponse(
        Long id,
        String status,
        String conteudo,
        String competencia
) {
    public static TrilhaResponse from(Trilha t) {
        return new TrilhaResponse(
                t.getId(),
                t.getStatus(),
                t.getConteudo(),
                t.getCompetencia()
        );
    }
}
