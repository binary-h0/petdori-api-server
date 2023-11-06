package petdori.apiserver.domain.walklog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecentlyLogResponseDto {
    private LocalDate startedTime;
    @JsonProperty("walked_distance")
    private BigDecimal walkedDistance;
}
