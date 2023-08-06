package wooyoungsoo.authserver.domain.auth.exception.token;

public class InvalidJwtException extends CustomJwtException {
    public InvalidJwtException() {
        super(CustomJwtErrorCode.INVALID_TOKEN);
    }
}
