package com.example.demo.dto.response;

import com.example.demo.model.Objetivo;

public record ObjetivoResponse(
        Long id,
        String cargo,
        String area,
        String descricao,
        String demanda
) {
    public static ObjetivoResponse from(Objetivo o) {
        return new ObjetivoResponse(
                o.getId(),
                o.getCargo(),
                o.getArea(),
                o.getDescricao(),
                o.getDemanda()
        );
    }
}
