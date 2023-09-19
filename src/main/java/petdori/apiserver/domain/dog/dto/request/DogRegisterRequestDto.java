package petdori.apiserver.domain.dog.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class DogRegisterRequestDto {
    private String dogName;
    private String dogType;
    private String dogGender;
    private Boolean isNeutered;
    private BigDecimal dogWeight;
    private String dogBirth;
}
