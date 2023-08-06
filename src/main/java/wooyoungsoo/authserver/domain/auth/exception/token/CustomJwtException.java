package wooyoungsoo.authserver.domain.auth.exception.token;

import lombok.Getter;

@Getter
public class CustomJwtException extends RuntimeException {
    private final CustomJwtErrorCode customJwtErrorCode;

    public CustomJwtException(CustomJwtErrorCode customJwtErrorCode) {
        super(customJwtErrorCode.getErrorMessage());
        this.customJwtErrorCode = customJwtErrorCode;
    }
}
