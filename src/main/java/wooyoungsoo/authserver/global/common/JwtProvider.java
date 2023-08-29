package wooyoungsoo.authserver.global.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import wooyoungsoo.authserver.domain.auth.entity.member.WYSMemberDetails;
import wooyoungsoo.authserver.domain.auth.exception.token.InvalidJwtException;
import wooyoungsoo.authserver.domain.auth.exception.token.InvalidJwtSignatureException;
import wooyoungsoo.authserver.domain.auth.exception.token.UnknownJwtException;
import wooyoungsoo.authserver.domain.auth.exception.token.ValidTimeExpiredJwtException;
import wooyoungsoo.authserver.domain.auth.service.WYSMemberDetailsService;
import java.util.stream.Collectors;
import java.security.Key;
import java.util.Date;


@Slf4j
@Component
public class JwtProvider {
    private final Key accessKey;
    private final Key refreshKey;
    private final Long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60; // 1시간
    private final Long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30; // 30일

    @Autowired
    public JwtProvider(@Value("${jwt.access.secret}") String accessSecretKey,
                       @Value("${jwt.refresh.secret}") String refreshSecretKey,
                       WYSMemberDetailsService WYSMemberDetailsService) {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretKey));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretKey));
    }

    public String createAccessToken(Authentication authentication) {
        String authority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        Claims claims = Jwts.claims();
        claims.put("role", authority);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(authentication.getName())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken() {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(accessToken);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException ex) {
            log.info("잘못된 JWT 서명입니다.");
            throw new InvalidJwtSignatureException();
        } catch (ExpiredJwtException ex) {
            log.info("만료된 JWT 토큰입니다.");
            throw new ValidTimeExpiredJwtException();
        } catch (UnsupportedJwtException ex) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw new UnknownJwtException();
        } catch (IllegalArgumentException ex) {
            log.info("JWT 토큰이 잘못됐습니다.");
            throw new InvalidJwtException();
        }
    }

    public void validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(refreshToken);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException ex) {
            log.info("잘못된 JWT 서명입니다.");
            throw new InvalidJwtSignatureException();
        } catch (ExpiredJwtException ex) {
            log.info("만료된 JWT 토큰입니다.");
            throw new ValidTimeExpiredJwtException();
        } catch (UnsupportedJwtException ex) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw new UnknownJwtException();
        } catch (IllegalArgumentException ex) {
            log.info("JWT 토큰이 잘못됐습니다.");
            throw new InvalidJwtException();
        }
    }

    public Authentication getAuthenticationFromToken(String accessToken) {
        String email = extractEmailFromToken(accessToken);
        String authority = extractAuthorityFromToken(accessToken);
        WYSMemberDetails WYSMemberDetails = new WYSMemberDetails(email, authority);

        return new UsernamePasswordAuthenticationToken(WYSMemberDetails,
                "", WYSMemberDetails.getAuthorities());
    }

    private String extractEmailFromToken(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims().getSubject();
        }
    }

    private String extractAuthorityFromToken(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .get("role", String.class);
        } catch (ExpiredJwtException ex) {
            return ex.getClaims().get("role", String.class);
        }
    }
}
