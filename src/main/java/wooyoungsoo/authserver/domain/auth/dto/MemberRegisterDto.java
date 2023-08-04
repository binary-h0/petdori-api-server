package wooyoungsoo.authserver.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberRegisterDto {
    private String email;
    private String name;
}
