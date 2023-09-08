package petdori.apiserver.domain.auth.exception.token;

public class UnknownJwtException extends CustomJwtException {
    public UnknownJwtException() {
        super(CustomJwtErrorCode.UNSUPPORTED_TOKEN);
    }
}
