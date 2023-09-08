package petdori.apiserver.domain.auth.exception.oauth2;

public class ClientIdNotMatchedException extends Oauth2Exception {
    public ClientIdNotMatchedException() {
        super(Oauth2ErrorCode.CLIENT_ID_NOT_MATCHED);
    }
}
