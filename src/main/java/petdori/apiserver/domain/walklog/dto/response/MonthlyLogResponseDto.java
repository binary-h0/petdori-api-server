package petdori.apiserver.domain.walklog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class MonthlyLogResponseDto {
    private Long id;

    @JsonProperty("walking_image_url")
    private String walkingImageUrl;

    @JsonProperty("started_time")
    private LocalDateTime startedTime;

    @JsonProperty("walking_time")
    private LocalTime walkingTime;

    @JsonProperty("walked_distance")
    private BigDecimal walkedDistance;

    MonthlyLogResponseDto(Long id, String walkingImageUrl, LocalDateTime startedTime, LocalTime walkingTime, BigDecimal walkedDistance) {
        this.id = id;
        this.walkingImageUrl = walkingImageUrl;
        this.startedTime = startedTime;
        this.walkingTime = walkingTime;
        this.walkedDistance = walkedDistance;
    }
}
