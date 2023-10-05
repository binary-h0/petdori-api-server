package petdori.apiserver.domain.dog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petdori.apiserver.domain.dog.entity.Dog;

import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Long> {
    Optional<Dog> findById(Long id);
}
