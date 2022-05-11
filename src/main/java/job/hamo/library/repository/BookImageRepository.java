package job.hamo.library.repository;

import job.hamo.library.entity.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookImageRepository extends JpaRepository<BookImage, UUID> {
    Optional<BookImage> findById(UUID id);

    Optional<BookImage> findByName(String name);
}
