package com.Projeto.InfoMaisSaude.services;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expirationHours:2}")
    private long expirationHours;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String gerarToken(Usuario usuario) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(expirationHours, ChronoUnit.HOURS);

        List<String> roles = usuario.getAuthorities().stream()
                                  .map(GrantedAuthority::getAuthority)
                                  .collect(Collectors.toList());

        return Jwts.builder()
                .setIssuer("InfoMaisSaude API") 
                .setSubject(usuario.getLogin()) 
                .setIssuedAt(Date.from(now)) 
                .setExpiration(Date.from(expirationTime)) 
                 .claim("role", roles.isEmpty() ? null : roles.get(0).replace("ROLE_", "")) 
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) 
                .compact();
    }

    public String getSubject(String tokenJWT) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(tokenJWT)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }
    
}
