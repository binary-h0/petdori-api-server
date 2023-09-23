package petdori.apiserver.domain.facility.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NearbyFacilityResponseDto {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String distanceInfo;
    private String operatingHourInfo;
}
