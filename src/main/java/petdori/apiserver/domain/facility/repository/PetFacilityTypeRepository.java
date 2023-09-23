package petdori.apiserver.domain.facility.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petdori.apiserver.domain.facility.entity.PetFacilityType;
import java.util.Optional;

public interface PetFacilityTypeRepository extends JpaRepository<PetFacilityType, Long> {
    Optional<PetFacilityType> findIdByTypeName(String typeName);
}
