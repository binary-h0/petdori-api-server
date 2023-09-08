package petdori.apiserver.domain.auth.exception.oauth2;

public class ClaimsNotReceivedException extends Oauth2Exception {
    public ClaimsNotReceivedException() {
        super(Oauth2ErrorCode.CLAIMS_NOT_RECEIVED);
    }
}
