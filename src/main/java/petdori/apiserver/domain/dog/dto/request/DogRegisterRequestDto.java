package petdori.apiserver.domain.dog.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DogRegisterRequestDto {
    private String dogName;
    private String dogType;
    private String dogGender;
    private Boolean isNeutered;
    private int dogAge;
}
