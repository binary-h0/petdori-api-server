package petdori.apiserver.domain.auth.exception.token;

public class RefreshTokenNotMatchedException extends CustomJwtException {
    public RefreshTokenNotMatchedException() {
        super(CustomJwtErrorCode.REFRESH_TONE_NOT_MATCHED);
    }
}
