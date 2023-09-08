package petdori.apiserver.domain.auth.entity.member;

public enum Oauth2Provider {
    NONE, KAKAO, GOOGLE, APPLE;

    public static Oauth2Provider getOauth2ProviderByName(String providerName) {
        return switch (providerName) {
            case "kakao" -> Oauth2Provider.KAKAO;
            case "google" -> Oauth2Provider.GOOGLE;
            case "apple" -> Oauth2Provider.APPLE;
            default -> Oauth2Provider.NONE;
        };
    }
}
