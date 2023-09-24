package petdori.apiserver.domain.facility.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NearbyFacilityResponseDto {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    @JsonProperty("distance_info")
    private String distanceInfo;
    @JsonProperty("operating_hour_info")
    private String operatingHourInfo;
}
