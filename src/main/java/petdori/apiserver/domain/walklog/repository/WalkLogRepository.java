package petdori.apiserver.domain.walklog.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import petdori.apiserver.domain.walklog.entity.WalkLog;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface WalkLogRepository extends JpaRepository<WalkLog, Long> {
    @Query(name = "recently_log_dto", nativeQuery = true,
            value = "WITH RECURSIVE RecentDates AS ( " +
                    "  SELECT CURDATE() AS logDate " +
                    "  UNION ALL " +
                    "  SELECT DATE_SUB(logDate, INTERVAL 1 DAY) " +
                    "  FROM RecentDates " +
                    "  WHERE logDate > DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                    ") " +
                    "SELECT rd.logDate AS walkDate, COALESCE(SUM(w.walked_distance), 0) AS totalWalkedDistance " +
                    "FROM RecentDates rd " +
                    "LEFT JOIN walk_log w ON w.member_id = ?1 AND rd.logDate = DATE(w.started_time) " +
                    "GROUP BY rd.logDate " +
                    "ORDER BY rd.logDate")
    List<RecentlyLogDto> findStartedTimeAndWalkedDistanceForLast30Days(Long memberId);

    interface RecentlyLogDto {
        @JsonProperty("walk_date")
        LocalDate getWalkDate();

        @JsonProperty("total_walked_distance")
        BigDecimal getTotalWalkedDistance();
    }
}
