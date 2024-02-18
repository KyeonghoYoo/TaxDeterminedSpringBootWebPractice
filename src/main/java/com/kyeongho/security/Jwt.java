package com.kyeongho.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT를 생성하거나 검증하는 역할을 함.
 * jsonwebtoken 라이브러리를 사용하여 구현됨.
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
@Slf4j
@Data
public final class Jwt {
    private final String issuer;
    private final SecretKey clientSecret;
    private final int expirySeconds;
    private final MacAlgorithm signatureAlgorithm;
    private final JwtParser jwtParser;

    public Jwt(final String issuer, final String clientSecret, final int expirySeconds) {
        this.issuer = issuer;
        this.clientSecret = Keys.hmacShaKeyFor(clientSecret.getBytes(StandardCharsets.UTF_8));
        this.expirySeconds = expirySeconds;
        this.signatureAlgorithm = Jwts.SIG.HS256;
        this.jwtParser = Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(this.clientSecret)
                .build();
    }

    /**
     * JWT 토큰을 생성한다.
     *
     * @param payload JWT의 payload 부분에 담길 값들을 지닌 Payload 객체
     * @return 생성된 JWT 토큰이 담긴 String 객체
     */
    public String create(Payload payload) {

        JwtBuilder builder = Jwts.builder()
                .header()
                    .type("JWT")
                .and()
                .issuer(this.issuer)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + this.expirySeconds * 1_000L))
                .claim("name", payload.getName())
                .claim("userId", payload.getUserId())
                .signWith(clientSecret, signatureAlgorithm);
        return builder.compact();
    }

    /**
     * JWT 토큰의 검증하고 검증이 완료되었다면 Claims를 추출해 반환한다.
     *
     * @param token 검증할 JWT 토큰
     * @return 추출된 header, payload를 담은 Payload 객체
     */
    public Payload verify(String token) {
        Jws<Claims> jws;
        try {
            jws = jwtParser.parseSignedClaims(token);
        } catch (SignatureException e) {
            log.error("Invalid JWT signature", e);
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token", e);
            throw e;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw e;
        } catch (JwtException e) {
            log.error("JWT Exception", e);
            throw e;
        }
        return new Payload(jws.getPayload());
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Payload {
        private String userId;
        private String name;
        private Date iat;
        private Date exp;

        public Payload(Claims claims) {
            this.userId = claims.get("userId", String.class);
            this.name = claims.get("name", String.class);
            this.iat = claims.getIssuedAt();
            this.exp = claims.getExpiration();
        }

        public static Payload of(final String name, final String userId) {
            Payload payload = new Payload();
            payload.setName(name);
            payload.setUserId(userId);
            return payload;
        }

        long getIat() {
            return iat != null ? iat.getTime() : -1;
        }

        void eraseIat() {
            iat = null;
        }

        void eraseExp() {
            exp = null;
        }
    }
}
