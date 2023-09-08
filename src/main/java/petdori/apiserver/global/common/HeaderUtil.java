package petdori.apiserver.global.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class HeaderUtil {
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String PREFIX_TOKEN = "Bearer ";

    public static String resolveTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(PREFIX_TOKEN)) {
            return bearerToken.substring(PREFIX_TOKEN.length());
        }
        return null;
    }
}
