package wooyoungsoo.authserver.domain.auth.exception.oauth2;

public class AppleKeyInfoNotReceivedException extends Oauth2Exception {
    public AppleKeyInfoNotReceivedException() {
        super(Oauth2ErrorCode.APPLE_KEY_INFO_NOT_RECEIVED);
    }
}
