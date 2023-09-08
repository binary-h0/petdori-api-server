package petdori.apiserver.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DogRegisterDto {
    private String ownerEmail;
    private String dogName;
    private String dogType;
    private String dogGender;
    private Boolean isNeutered;
    private int dogAge;
}
