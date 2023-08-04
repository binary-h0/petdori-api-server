package wooyoungsoo.authserver.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DogTypeNotExistResponseDto {
    private String typeName;
}
