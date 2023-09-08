package petdori.apiserver.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import petdori.apiserver.global.common.HeaderUtil;
import petdori.apiserver.global.common.JwtProvider;
import petdori.apiserver.domain.auth.exception.token.CustomJwtException;
import petdori.apiserver.domain.auth.exception.token.ValidTimeExpiredJwtException;

import java.io.IOException;

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
            if (!request.getRequestURI().equals(REISSUE_PATH)) {
                request.setAttribute("exception", ex.getMessage());
                return;
            }
        } catch (CustomJwtException ex) {
            request.setAttribute("exception", ex.getMessage());
            return;
        }
        // accessToken검증을 통과한 경우 or 만료됐으나 재발급요청이었던 경우 통행증 발급 -> 인증되게끔 한다
        Authentication authentication = jwtProvider.getAuthenticationFromToken(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("저장 완료");
    }
}
