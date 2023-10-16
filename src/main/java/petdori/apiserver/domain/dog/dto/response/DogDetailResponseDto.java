package petdori.apiserver.domain.dog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class DogDetailResponseDto {
    @JsonProperty("dog_id")
    private Long dogId;
    @JsonProperty("dog_image_url")
    private String dogImageUrl;
    @JsonProperty("dog_name")
    private String dogName;
    @JsonProperty("dog_type_name")
    private String dogTypeName;
    @JsonProperty("dog_gender")
    private String dogGender;
    @JsonProperty("is_neutered")
    private Boolean isNeutered;
    @JsonProperty("dog_birth")
    private LocalDate dogBirth;
    @JsonProperty("dog_weight")
    private BigDecimal dogWeight;
}
