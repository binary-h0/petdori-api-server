package petdori.apiserver.domain.dog.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DogRegisterRequestDto {
    private String dogName;
    private String dogType;
    private String dogGender;
    private Boolean isNeutered;
    private String dogBirth;
}
