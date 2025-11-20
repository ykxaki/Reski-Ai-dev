package com.example.demo.controller;

import com.example.demo.dto.request.AuthRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.UsuarioResponse;
import com.example.demo.model.Usuario;
import com.example.demo.service.TokenService;
import com.example.demo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UsuarioService usuarioService;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authManager, UsuarioService usuarioService, TokenService tokenService) {
        this.authManager = authManager;
        this.usuarioService = usuarioService;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Registrar")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        var u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(req.email());
        u.setCpf(req.cpf().replaceAll("\\D",""));
        u.setSenha(req.senha());
        var salvo = usuarioService.createUsuario(u);

        var token = tokenService.generateAccessToken(usuarioService.toUserDetails(salvo), salvo.getCpf());
        var cookie = ResponseCookie.from("access_token", token).httpOnly(true).path("/").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("token", token, "usuario", UsuarioResponse.from(salvo)));
    }


    @Operation(summary = "Login (email + senha)")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.senha())
            );

            var usuario = usuarioService.readUsuarioByEmail(auth.getName());

            var token = tokenService.generateAccessToken(
                    usuarioService.toUserDetails(usuario),
                    usuario.getCpf()
            );
            var cookie = ResponseCookie.from("access_token", token)
                    .httpOnly(true)
                    .path("/")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("token", token, "usuario", UsuarioResponse.from(usuario)));

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body(
                    Map.of("error", "Email ou senha inválidos")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                    Map.of("error", "Erro interno ao tentar autenticar")
            );
        }
    }



    @Operation(summary = "Me (dados do usuário atual)")
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        var usuario = usuarioService.readUsuarioByEmail(auth.getName());
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @Operation(summary = "Logout (revoga token atual)")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String header) {
        tokenService.revoke(header);
        var cookie = ResponseCookie.from("access_token", "").httpOnly(true).path("/").maxAge(0).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logout realizado"));
    }
}
