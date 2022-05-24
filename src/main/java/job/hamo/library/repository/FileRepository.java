package job.hamo.library.repository;

import job.hamo.library.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findById(Long id);

    @Query(value = "SELECT f.path FROM File AS f WHERE f.book.id = :id AND f.imageSize = 'big'")
    Optional<String> findBookImagePathByBookIdAndImageSize(@Param("id") Long id);
}
