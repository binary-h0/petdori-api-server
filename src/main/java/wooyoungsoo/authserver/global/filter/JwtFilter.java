package wooyoungsoo.authserver.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import wooyoungsoo.authserver.domain.auth.exception.token.CustomJwtException;
import wooyoungsoo.authserver.domain.auth.exception.token.ValidTimeExpiredJwtException;
import wooyoungsoo.authserver.global.common.JwtProvider;
import wooyoungsoo.authserver.global.common.HeaderUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static wooyoungsoo.authserver.domain.auth.entity.member.Role.ROLE_USER;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final String REISSUE_PATH = "/api/auth/reissue";

    // 토큰의 인증정보를 SecurityContext 안에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        log.info("필터를 거칩니다");
        String accessToken = HeaderUtil.resolveTokenFromHeader(request);

        if (accessToken != null) {
            setAuthenticationToContextHolder(request, accessToken);
        }

        log.info("다음 필터로 갑니다");
        filterChain.doFilter(request, response);
    }

    private void setAuthenticationToContextHolder(HttpServletRequest request, String accessToken) {
        try {
            jwtProvider.validateAccessToken(accessToken);
        } catch (ValidTimeExpiredJwtException ex) {
            // access token이 만료됐지만 재발급하는 요청에 대한 처리 - 임시로 인증됐다고 처리한다
            if (request.getRequestURI().equals(REISSUE_PATH)) {
                setTemporaryAuthenticationToContextHolder();
                return;
            }
            request.setAttribute("exception", ex.getMessage());
            return;
        } catch (CustomJwtException ex) {
            request.setAttribute("exception", ex.getMessage());
            return;
        }

        Authentication authentication = jwtProvider.getAuthenticationFromToken(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("저장 완료");
    }

    private void setTemporaryAuthenticationToContextHolder() {
        // 임시 권한 생성
        List<GrantedAuthority> temporaryAuthorities = new ArrayList<>();
        temporaryAuthorities.add(new SimpleGrantedAuthority(ROLE_USER.name()));
        // 임시 통행증 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "temporaryAuthentication", "", temporaryAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
