package petdori.apiserver.domain.auth.exception.oauth2;

public class ApplePublicKeyNotGenerateException extends Oauth2Exception {
    public ApplePublicKeyNotGenerateException() {
        super(Oauth2ErrorCode.APPLE_PUBLIC_KEY_NOT_GENERATED);
    }
}
