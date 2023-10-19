package petdori.apiserver.domain.auth.entity.member;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import petdori.apiserver.domain.auth.dto.request.SignupRequestDto;
import petdori.apiserver.domain.dog.entity.Dog;
import petdori.apiserver.global.common.BaseTimeEntity;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted_date = NOW() WHERE id = ?")
@Builder
@Getter
@Entity
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 45)
    private String email;

    @Column(nullable = true, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "oauth2_provider")
    private Oauth2Provider oauth2Provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = true, length = 255)
    private String profileImageUrl;

    @Column(precision = 5, scale = 2, nullable = true)
    private BigDecimal weeklyWalkDistanceGoal;

    @Column(nullable = true)
    private int weeklyWalkNumberGoal;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Dog> dogs;

    public static Member from(Oauth2Provider oauth2Provider,
                              String profileImageUrl,
                              String email,
                              String name) {
        return Member.builder()
                .profileImageUrl(profileImageUrl)
                .email(email)
                .name(name)
                .oauth2Provider(oauth2Provider)
                .role(Role.ROLE_USER)
                .build();
    }
}
