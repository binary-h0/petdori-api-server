package wooyoungsoo.authserver.domain.auth.exception.token;

public class UnknownJwtException extends CustomJwtException {
    public UnknownJwtException() {
        super(CustomJwtErrorCode.UNSUPPORTED_TOKEN);
    }
}
