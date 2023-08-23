package wooyoungsoo.authserver.domain.auth.exception.oauth2;

public class MatchedKeyNotFoundException extends Oauth2Exception {
    public MatchedKeyNotFoundException() {
        super(Oauth2ErrorCode.MATCHED_KEY_NOT_FOUND);
    }
}
