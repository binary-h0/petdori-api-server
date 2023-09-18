package petdori.apiserver.domain.auth.dto.request;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SignupRequestDto {
    private String email;
    private String name;
}
