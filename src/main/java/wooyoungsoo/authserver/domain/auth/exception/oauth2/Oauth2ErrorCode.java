package wooyoungsoo.authserver.domain.auth.exception.oauth2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum Oauth2ErrorCode {
    APPLE_KEY_INFO_NOT_RECEIVED(HttpStatus.INTERNAL_SERVER_ERROR, "애플에서 키 정보들을 받아오지 못했습니다"),
    MATCHED_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 키 정보를 찾을 수 없습니다"),
    APPLE_PUBLIC_KEY_NOT_GENERATED(HttpStatus.INTERNAL_SERVER_ERROR, "키 생성에 실패했습니다"),
    CLAIMS_NOT_RECEIVED(HttpStatus.INTERNAL_SERVER_ERROR, "토큰으로부터 클레임을 받아오지 못했습니다"),
    ISSUER_NOT_MATCHED(HttpStatus.BAD_REQUEST, "발급자 정보가 일치하지 않습니다"),
    CLIENT_ID_NOT_MATCHED(HttpStatus.BAD_REQUEST, "대상자 정보가 일치하지 않습니다");

    private HttpStatus httpStatus;
    private String errorMessage;
}
