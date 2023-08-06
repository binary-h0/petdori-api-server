package wooyoungsoo.authserver.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import wooyoungsoo.authserver.global.common.BaseTimeEntity;
import wooyoungsoo.authserver.domain.auth.entity.member.Member;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE refresh_token SET deleted_date = NOW() WHERE id = ?")
@Builder
@Getter
@Setter
@Entity
public class RefreshToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Member member;

    private String tokenValue;
}
