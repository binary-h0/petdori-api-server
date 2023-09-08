package petdori.apiserver.domain.auth.exception.token;

public class InvalidJwtSignatureException extends CustomJwtException {
    public InvalidJwtSignatureException() {
        super(CustomJwtErrorCode.INVALID_SIGNATURE);
    }
}
