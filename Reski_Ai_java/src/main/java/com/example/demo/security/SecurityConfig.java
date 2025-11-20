package com.example.demo.security;

import com.example.demo.service.TokenService;
import com.example.demo.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    TokenService tokenService,
                                    UsuarioService usuarioService) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(m -> m.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(reg -> reg
                .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()

                .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                .requestMatchers("/h2-console/**").permitAll()

                .requestMatchers("/chat").permitAll()
                .requestMatchers("/objetivos", "/objetivos/**").permitAll()
                .requestMatchers("/trilhas", "/trilhas/**").permitAll()

                        .anyRequest().authenticated()
        );

        http.headers(h -> h.frameOptions(f -> f.disable()));

        http.addFilterBefore(new JwtAuthFilter(tokenService, usuarioService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    DaoAuthenticationProvider daoAuthProvider(UsuarioService usuarioService, PasswordEncoder encoder) {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(usuarioService);
        p.setPasswordEncoder(encoder);
        return p;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
