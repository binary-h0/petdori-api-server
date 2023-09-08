package petdori.apiserver.domain.auth.entity.member;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import petdori.apiserver.domain.auth.dto.MemberRegisterDto;
import petdori.apiserver.domain.auth.entity.dog.Dog;
import petdori.apiserver.global.common.BaseTimeEntity;

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

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Dog> dogs;

    public static Member from(Oauth2Provider oauth2Provider,
                              MemberRegisterDto memberRegisterDto) {
        return Member.builder()
                .email(memberRegisterDto.getEmail())
                .name(memberRegisterDto.getName())
                .oauth2Provider(oauth2Provider)
                .role(Role.ROLE_USER)
                .build();
    }
}
