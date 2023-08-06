package wooyoungsoo.authserver.domain.auth.exception.token;

public class ValidTimeExpiredJwtException extends CustomJwtException {
    public ValidTimeExpiredJwtException() {
        super(CustomJwtErrorCode.EXPIRED_TOKEN);
    }
}
