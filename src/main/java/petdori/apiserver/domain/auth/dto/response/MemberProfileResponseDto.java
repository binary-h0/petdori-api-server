package petdori.apiserver.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberProfileResponseDto {
    private String name;
    private String email;
    private String provider;
    @JsonProperty("profile_image_url")
    private String profileImageUrl;
}
