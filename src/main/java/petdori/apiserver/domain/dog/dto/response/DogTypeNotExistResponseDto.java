package petdori.apiserver.domain.dog.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DogTypeNotExistResponseDto {
    private String typeName;
}
