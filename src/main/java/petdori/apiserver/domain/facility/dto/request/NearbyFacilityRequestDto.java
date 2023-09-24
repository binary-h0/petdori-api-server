package petdori.apiserver.domain.facility.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NearbyFacilityRequestDto {
    // 위도
    private double latitude;
    // 경도
    private double longitude;
    // 반경 (단위 : m)
    private Long radius;
}
