package petdori.apiserver.domain.facility.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petdori.apiserver.domain.facility.entity.PetFacilityOperatingHour;
import java.util.List;

public interface PetFacilityOperatingHourRepository extends JpaRepository<PetFacilityOperatingHour, Long> {
    List<PetFacilityOperatingHour> findByPetFacilityId(Long petFacilityId);
}
