package petdori.apiserver.domain.auth.exception.oauth2;

import lombok.Getter;

@Getter
public class Oauth2Exception extends RuntimeException {
    private final Oauth2ErrorCode oauth2ErrorCode;

    public Oauth2Exception(Oauth2ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.oauth2ErrorCode = errorCode;
    }
}
