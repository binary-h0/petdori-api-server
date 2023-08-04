package wooyoungsoo.authserver.domain.auth.entity.dog;

import jakarta.persistence.*;
import lombok.Getter;
import wooyoungsoo.authserver.global.common.BaseTimeEntity;

@Getter
@Entity
public class DogType extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45, unique = true)
    private String typeName;
}
