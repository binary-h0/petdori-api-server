package petdori.apiserver.domain.walklog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import petdori.apiserver.domain.auth.entity.member.Member;
import petdori.apiserver.global.common.BaseTimeEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE walk_log SET deleted_date = NOW() WHERE id = ?")
@Where(clause = "deleted_date IS NULL")
@Builder
@Getter
@Setter
@Entity
public class WalkLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal walkedDistance;

    @Column(nullable = false)
    private LocalDateTime startedTime;

    @Column(nullable = false)
    private LocalTime walkingTime;

    @Column(nullable = true, length = 255)
    private String walkingRouteFileUrl;

    @Column(nullable = true, length = 255)
    private String walkingImageUrl;



}
