package wooyoungsoo.authserver.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wooyoungsoo.authserver.domain.auth.entity.dog.DogType;

import java.util.Optional;

public interface DogTypeRepository extends JpaRepository<DogType, Long> {
    Optional<DogType> findByTypeName(String typeName);
}
