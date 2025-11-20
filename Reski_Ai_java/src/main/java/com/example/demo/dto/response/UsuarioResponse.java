package com.example.demo.dto.response;

import com.example.demo.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String cpf
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getCpf()
        );
    }
}
