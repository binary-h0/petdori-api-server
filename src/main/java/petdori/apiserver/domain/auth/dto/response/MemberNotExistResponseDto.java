package petdori.apiserver.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberNotExistResponseDto {
    private String email;
}
