package org.ahv.passwordprotectorback.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.function.Function;

@Component
public class JWTService {
    private final byte[] SECRET_KEY = generateSecretKey();
    private final byte[] REFRESH_SECRET_KEY = generateSecretKey();
    public final int TOKEN_EXPIRATION_TIME = 300000; //5m
    public final int REFRESH_TOKEN_EXPIRATION_TIME = 1800000; //30m

    public Claims extractAllClaims(String token, boolean refresh) {
        return Jwts.parserBuilder()
                .setSigningKey(refresh ? REFRESH_SECRET_KEY : SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + TOKEN_EXPIRATION_TIME); // 5 minutes

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY))
                .compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME); // 5 minutes

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(Keys.hmacShaKeyFor(REFRESH_SECRET_KEY))
                .compact();
    }

    private byte[] generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T extractClaim(String token, boolean refresh, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, refresh);
        return claimsResolver.apply(claims);
    }
}