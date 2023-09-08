package petdori.apiserver.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petdori.apiserver.domain.auth.entity.dog.Dog;

public interface DogRepository extends JpaRepository<Dog, Long> {
}
