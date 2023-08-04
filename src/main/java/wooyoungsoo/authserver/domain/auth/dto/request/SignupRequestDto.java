package wooyoungsoo.authserver.domain.auth.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import wooyoungsoo.authserver.domain.auth.dto.DogRegisterDto;
import wooyoungsoo.authserver.domain.auth.dto.MemberRegisterDto;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SignupRequestDto {
    String email;
    String name;
    String dogName;
    String dogType;
    String dogGender;
    int dogAge;

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
                .dogAge(dogAge)
                .build();
    }
}
