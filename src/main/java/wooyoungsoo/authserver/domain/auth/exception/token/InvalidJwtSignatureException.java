package wooyoungsoo.authserver.domain.auth.exception.token;

public class InvalidJwtSignatureException extends CustomJwtException {
    public InvalidJwtSignatureException() {
        super(CustomJwtErrorCode.INVALID_SIGNATURE);
    }
}
