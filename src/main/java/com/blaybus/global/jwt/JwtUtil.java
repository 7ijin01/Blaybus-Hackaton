package com.blaybus.global.jwt;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000; // 7일

    //객체 키 생성
    private SecretKey secretKey;

    //검증 메서드

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("googleId", String.class);
    }
    public String getName(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("name", String.class);
    }



    public Boolean isExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();

            if (expiration == null) {
                throw new Exception(token);
            }

            return expiration.before(new Date());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public String createAccess(String googleId, String name) {
        return Jwts.builder()
                .claim("email", googleId)
                .claim("name", name)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String createRefresh(String googleId, String name) {
        return Jwts.builder()
                .claim("email", googleId)
                .claim("name", name)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseSignedClaims(token)
                .getBody();

    }
    public Authentication getAuthentication(String token) {
        Claims claims = extractClaims(token);

        UserDetails user = new User(claims.getSubject(), "", Collections.emptyList());

        return new UsernamePasswordAuthenticationToken(user, "", Collections.emptyList());
    }

//    public String processToken(String accessToken, HttpServletRequest request){
//        String refreshToken = CookieUtil.findTokenOrThrow(request);
//        if (!isExpired(accessToken) && !isExpired(refreshToken)){
//            return createRefresh(getEmail(accessToken));
//        }
//
//        return null;
//    }
}
