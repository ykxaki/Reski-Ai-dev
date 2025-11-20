package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Usuario createUsuario(Usuario u) {
        u.setCpf(u.getCpf() == null ? null : u.getCpf().replaceAll("\\D",""));
        u.setSenha(encoder.encode(u.getSenha()));
        return repo.save(u);
    }

    public Usuario readUsuarioByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public Usuario readUsuarioById(Long id) {
        return repo.findById(id).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public void deleteUsuario(Long id) {
        repo.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = readUsuarioByEmail(username);
        return toUserDetails(u);
    }

    public UserDetails toUserDetails(Usuario u) {
        return User.withUsername(u.getEmail())
                .password(u.getSenha())
                .authorities(Collections.<GrantedAuthority>emptyList())
                .build();
    }

    public Page<Usuario> readUsuarios(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Usuario updateUsuario(Long id, Usuario parcial) {
        return repo.findById(id).map(u -> {
            if (parcial.getNome() != null)  u.setNome(parcial.getNome());
            if (parcial.getEmail() != null) u.setEmail(parcial.getEmail());
            if (parcial.getSenha() != null) u.setSenha(encoder.encode(parcial.getSenha()));
            return repo.save(u);
        }).orElse(null);
    }
}
