package petdori.apiserver.domain.dog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyDogResponseDto {
    @JsonProperty("dog_id")
    private Long dogId;
    @JsonProperty("dog_image_url")
    private String dogImageUrl;
    @JsonProperty("dog_name")
    private String dogName;
    @JsonProperty("dog_type_name")
    private String dogTypeName;
}
