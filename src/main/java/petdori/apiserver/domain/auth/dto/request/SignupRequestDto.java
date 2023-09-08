package petdori.apiserver.domain.auth.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import petdori.apiserver.domain.auth.dto.DogRegisterDto;
import petdori.apiserver.domain.auth.dto.MemberRegisterDto;

@Slf4j
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SignupRequestDto {
    private String email;
    private String name;
    private String dogName;
    private String dogType;
    private String dogGender;
    private Boolean isNeutered;
    private int dogAge;

    public MemberRegisterDto toMemberRegisterDto() {
        return MemberRegisterDto.builder()
                .email(email)
                .name(name)
                .build();
    }

    public DogRegisterDto toDogRegisterDto() {
        return DogRegisterDto.builder()
                .ownerEmail(email)
                .dogName(dogName)
                .dogType(dogType)
                .dogGender(dogGender)
                .isNeutered(isNeutered)
                .dogAge(dogAge)
                .build();
    }
}
