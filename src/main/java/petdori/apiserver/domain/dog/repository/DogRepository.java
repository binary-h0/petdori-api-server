package petdori.apiserver.domain.dog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petdori.apiserver.domain.dog.entity.Dog;

public interface DogRepository extends JpaRepository<Dog, Long> {
}
