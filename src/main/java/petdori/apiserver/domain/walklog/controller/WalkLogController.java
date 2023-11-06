package petdori.apiserver.domain.walklog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import petdori.apiserver.domain.walklog.dto.response.MonthlyLogResponseDto;
import petdori.apiserver.domain.walklog.repository.WalkLogRepository.RecentlyLogDto;
import petdori.apiserver.domain.walklog.service.WalkLogService;
import petdori.apiserver.global.common.BaseResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/walk-log")
public class WalkLogController {
    private final WalkLogService walkLogService;

    @GetMapping("/recently-logs")
    private BaseResponse<List<RecentlyLogDto>> getRecentlyLogs() {
        List<RecentlyLogDto> recentlyLogs = walkLogService
                .getWalkLogsForLast30days();
        return BaseResponse.createSuccessResponse(recentlyLogs);
    }

    @GetMapping("/monthly-logs")
    private BaseResponse<List<MonthlyLogResponseDto>> getMonthlyLogs(@RequestParam(value = "year") int year,
                                                                     @RequestParam(value = "month") int month) {
        List<MonthlyLogResponseDto> monthlyLogs = walkLogService.getMonthlyLogs(year, month);
        return BaseResponse.createSuccessResponse(monthlyLogs);
    }

}
