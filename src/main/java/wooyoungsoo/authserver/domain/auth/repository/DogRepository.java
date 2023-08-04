package wooyoungsoo.authserver.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wooyoungsoo.authserver.domain.auth.entity.dog.Dog;

public interface DogRepository extends JpaRepository<Dog, Long> {
}
