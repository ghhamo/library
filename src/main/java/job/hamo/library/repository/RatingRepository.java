package job.hamo.library.repository;

import job.hamo.library.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findById(Long id);
}
