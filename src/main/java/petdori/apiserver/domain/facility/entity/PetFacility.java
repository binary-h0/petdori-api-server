package petdori.apiserver.domain.facility.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.locationtech.jts.geom.Point;
import petdori.apiserver.global.common.BaseTimeEntity;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE pet_facility SET deleted_date = NOW() WHERE id = ?")
@Builder
@Getter
@Entity
public class PetFacility extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @OneToOne
    @JoinColumn(name = "pet_facility_type_id", nullable = false)
    private PetFacilityType petFacilityType;

    @Column(nullable = false, columnDefinition = "POINT SRID 4326")
    private Point location;

    @Column(nullable = false, length = 255)
    private String address;

    @OneToMany(mappedBy = "petFacility", cascade = CascadeType.ALL)
    private List<PetFacilityOperatingHour> operatingHours;
}
