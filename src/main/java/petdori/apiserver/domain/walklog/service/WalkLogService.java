package petdori.apiserver.domain.walklog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import petdori.apiserver.domain.auth.entity.member.Member;
import petdori.apiserver.domain.auth.exception.member.MemberNotExistException;
import petdori.apiserver.domain.auth.repository.MemberRepository;
import petdori.apiserver.domain.walklog.dto.response.MonthlyLogResponseDto;
import petdori.apiserver.domain.walklog.entity.WalkLog;
import petdori.apiserver.domain.walklog.repository.WalkLogRepository;
import petdori.apiserver.domain.walklog.repository.WalkLogRepository.RecentlyLogDto;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class WalkLogService {
    private final MemberRepository memberRepository;
    private final WalkLogRepository walkLogRepository;

    public List<RecentlyLogDto> getWalkLogsForLast30days() {
        // 인증된 사용자이므로 이메일을 가져올 수 있다
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new MemberNotExistException(memberEmail));

        return walkLogRepository
                .findStartedTimeAndWalkedDistanceForLast30Days(member.getId());
    }

    public List<MonthlyLogResponseDto> getMonthlyLogs(int year, int month) {
        // 인증된 사용자이므로 이메일을 가져올 수 있다
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new MemberNotExistException(memberEmail));

        return walkLogRepository.findByYearAndMonth(member, year, month);
    }
}
