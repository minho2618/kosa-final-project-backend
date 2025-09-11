package org.kosa.jwt;


import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.User;
import org.kosa.enums.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/*
 JWT 정보 검증및 생성
 */
@Component
@Slf4j
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
   
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Long getUserId(String token) {
        log.info("getUserId(String token)  call");
        Long re = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
        log.info("getUserId(String token)  re = {}" ,re);
        return re;
    }

    //검증 Username
    public String getUsername(String token) {
         log.info("getUsername(String token)  call");
        String re = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
        log.info("getUsername(String token)  re = {}" ,re);
        return re;
    }
    //검증 Id
    public String getEmail(String token) {
        log.info("getEmail(String token)  call");
        String re = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
        log.info("getEmail(String token)  re = {}" ,re);
        return re;
    }
    
    //검증 Role
    public String getRole(String token) {
        log.info("getRole(String token)  call");
        String re = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
        log.info("getRole(String token)  re = {} " , re);
        return re;
    }
    
    //검증 Expired
    public Boolean isExpired(String token) {
        log.info("isExpired(String token)  call");
        boolean re = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        log.info("isExpired(String token)  re  = {}",re);
        return re;
    }

    public String createJwt(User user, String role, Long expiredMs) {
        log.info("createJwt  call");
        return Jwts.builder()
                .claim("userId", user.getUserId())//멤버번호
                .claim("username", user.getName()) //이름
                .claim("email", user.getEmail()) //아이디
                .claim("role", role) //Role
                .issuedAt(new Date(System.currentTimeMillis())) //현재로그인된 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) //만료시간
                .signWith(secretKey)
                .compact();
    }
}



