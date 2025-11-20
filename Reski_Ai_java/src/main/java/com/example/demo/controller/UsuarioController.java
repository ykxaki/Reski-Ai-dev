package com.example.demo.controller;

import com.example.demo.dto.request.UsuarioUpdateRequest;
import com.example.demo.dto.response.UsuarioResponse;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/usuarios")
@Tag(name = "api-usuarios-java")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Lista todos os usuários (paginado)")
    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> readUsuarios(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "nome,asc") String sort
    ) {
        Sort s = Sort.by(sort.split(",")[0]).ascending();
        if (sort.toLowerCase().endsWith(",desc")) s = s.descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, s);
        Page<Usuario> usuariosPage = usuarioService.readUsuarios(pageable);
        Page<UsuarioResponse> responses = usuariosPage.map(UsuarioResponse::from);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Retorna um usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> readUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.readUsuarioById(id);
        return (usuario != null)
                ? ResponseEntity.ok(UsuarioResponse.from(usuario))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Atualiza um usuário (ao atualizar faça o login novamente)")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request
    ) {
        Usuario parcial = new Usuario();
        parcial.setNome(request.nome());
        parcial.setEmail(request.email());
        parcial.setSenha(request.senha());

        Usuario salvo = usuarioService.updateUsuario(id, parcial);
        return (salvo != null)
                ? ResponseEntity.ok(UsuarioResponse.from(salvo))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(summary = "Exclui um usuário por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        Usuario existente = usuarioService.readUsuarioById(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
