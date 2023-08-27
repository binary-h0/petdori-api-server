package wooyoungsoo.authserver.domain.auth.exception.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CustomJwtErrorCode {
    INVALID_SIGNATURE(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 JWT 서명입니다"),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "만료된 JWT 토큰입니다"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰입니다"),
    REFRESH_TONE_NOT_MATCHED(HttpStatus.BAD_REQUEST, "사용할 수 없는 토큰입니다");

    private HttpStatus httpStatus;
    private String errorMessage;
}
