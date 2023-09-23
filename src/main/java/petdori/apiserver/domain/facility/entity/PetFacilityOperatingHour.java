package petdori.apiserver.domain.facility.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import java.sql.Time;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE pet_facility_operating_hour SET deleted_date = NOW() WHERE id = ?")
@Builder
@Getter
@Entity
public class PetFacilityOperatingHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_facility_id", nullable = false)
    private PetFacility petFacility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfTheWeek dayOfTheWeek;

    @Column(nullable = false, columnDefinition = "TIME")
    private String openHour;

    @Column(nullable = false, columnDefinition = "TIME")
    private String closeHour;
}
