package com.blaybus.global.jwt;



import com.blaybus.domain.register.entity.User;
import com.blaybus.domain.register.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class JwtUtil {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000; // 7일

    //객체 키 생성
    private final SecretKey secretKey;
    private final UserRepository userRepository;

    //검증 메서드

    public JwtUtil(@Value("${jwt.secret}") String secret, UserRepository userRepository) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.userRepository = userRepository;
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
                .claim("googleId",googleId)
                .claim("name", name)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String createRefresh(String googleId, String name) {
        return Jwts.builder()
                .claim("googleId", googleId)
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
        log.info("claims: ", claims);
        String googleId = claims.getSubject();  // ✅ JWT의 "sub" 값을 googleId로 가져옴
        System.out.println(googleId);
        if (googleId == null || googleId.isEmpty()) {
            throw new IllegalArgumentException("JWT token does not contain a valid googleId.");
        }

        // DB에서 googleId 기반으로 사용자 찾기
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByGoogleId(googleId));
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with googleId: " + googleId);
        }

        User user = userOptional.get();
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Spring Security User 객체 생성 (googleId를 username으로 사용)
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getGoogleId(), "", authorities
        );

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
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
