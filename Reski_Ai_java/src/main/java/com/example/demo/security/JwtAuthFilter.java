package com.example.demo.security;

import com.example.demo.service.TokenService;
import com.example.demo.service.UsuarioService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public JwtAuthFilter(TokenService tokenService, UsuarioService usuarioService) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (StringUtils.hasText(token)) {
                DecodedJWT jwt = tokenService.validate(token);
                var email = jwt.getSubject();
                var userDetails = usuarioService.loadUserByUsername(email);
                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header)) return header;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("access_token".equals(c.getName()) && StringUtils.hasText(c.getValue())) {
                    return "Bearer " + c.getValue();
                }
            }
        }
        return null;
    }
}
