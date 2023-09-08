package petdori.apiserver.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petdori.apiserver.domain.auth.entity.dog.DogType;

import java.util.Optional;

public interface DogTypeRepository extends JpaRepository<DogType, Long> {
    Optional<DogType> findByTypeName(String typeName);
}
