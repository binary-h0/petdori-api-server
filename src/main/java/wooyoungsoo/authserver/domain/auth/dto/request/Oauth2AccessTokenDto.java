package wooyoungsoo.authserver.domain.auth.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
public class Oauth2AccessTokenDto {
    private String accessToken;
}
