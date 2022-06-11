package job.hamo.library.repository;

import job.hamo.library.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findById(Long id);
    Optional<Genre> findByName(String name);

}
