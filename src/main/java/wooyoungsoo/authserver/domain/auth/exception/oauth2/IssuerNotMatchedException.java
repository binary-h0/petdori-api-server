package wooyoungsoo.authserver.domain.auth.exception.oauth2;

public class IssuerNotMatchedException extends Oauth2Exception {
    public IssuerNotMatchedException() {
        super(Oauth2ErrorCode.ISSUER_NOT_MATCHED);
    }
}
