package petdori.apiserver.domain.dog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petdori.apiserver.domain.dog.entity.DogType;
import java.util.Optional;

public interface DogTypeRepository extends JpaRepository<DogType, Long> {
    Optional<DogType> findByTypeName(String typeName);
}
