package petdori.apiserver.domain.facility.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FacilityTypeNotExistResponseDto {
    @JsonProperty("type_name")
    private String typeName;
}
