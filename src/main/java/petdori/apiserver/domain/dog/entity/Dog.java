package petdori.apiserver.domain.dog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import petdori.apiserver.domain.dog.dto.request.DogRegisterRequestDto;
import petdori.apiserver.global.common.BaseTimeEntity;
import petdori.apiserver.domain.auth.entity.member.Member;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE dog SET deleted_date = NOW() WHERE id = ?")
@Builder
@Getter
@Entity
public class Dog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @Column(nullable = false, length = 20)
    private String dogName;

    @OneToOne
    @JoinColumn(name = "dog_type_id", nullable = false)
    private DogType dogType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DogGender dogGender;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isNeutered;

    @Column(nullable = false)
    private int dogAge;

    public static Dog from(Member owner,
                           DogType dogType,
                           DogRegisterRequestDto dogRegisterRequestDto) {
        return Dog.builder()
                .owner(owner)
                .dogName(dogRegisterRequestDto.getDogName())
                .dogType(dogType)
                .dogGender(DogGender.getDogGenderByGenderName(
                        dogRegisterRequestDto.getDogGender()
                ))
                .isNeutered(dogRegisterRequestDto.getIsNeutered())
                .dogAge(dogRegisterRequestDto.getDogAge())
                .build();
    }
}
