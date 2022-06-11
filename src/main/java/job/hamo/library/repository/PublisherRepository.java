package job.hamo.library.repository;

import job.hamo.library.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findById(Long id);
    Optional<Publisher> findByName(String name);
}
