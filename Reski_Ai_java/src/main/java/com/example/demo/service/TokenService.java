package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final String issuer;
    private final long accessMinutes;
    private final Set<String> blacklist = Collections.newSetFromMap(new WeakHashMap<>());

    public TokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer:api}") String issuer,
            @Value("${jwt.access-minutes:60}") long accessMinutes
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
        this.accessMinutes = accessMinutes;
        this.verifier = JWT.require(algorithm).withIssuer(issuer).build();
    }

    public String generateAccessToken(UserDetails user, String cpf) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getUsername())
                .withClaim("cpf", cpf == null ? "" : cpf)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(accessMinutes, ChronoUnit.MINUTES)))
                .sign(algorithm);
    }

    public DecodedJWT validate(String bearerOrRaw) {
        String raw = stripBearer(bearerOrRaw);
        if (raw == null || raw.isBlank()) throw new IllegalArgumentException("Token ausente");
        if (blacklist.contains(raw)) throw new IllegalArgumentException("Token revogado");
        return verifier.verify(raw);
    }

    public void revoke(String bearerOrRaw) {
        String raw = stripBearer(bearerOrRaw);
        if (raw != null) blacklist.add(raw);
    }

    public static String stripBearer(String token) {
        if (token == null) return null;
        String t = token.trim();
        return t.regionMatches(true, 0, "Bearer ", 0, 7) ? t.substring(7).trim() : t;
    }

    public String getCpf(DecodedJWT jwt) { return jwt.getClaim("cpf").asString(); }
}
