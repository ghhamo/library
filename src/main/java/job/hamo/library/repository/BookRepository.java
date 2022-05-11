package job.hamo.library.repository;

import job.hamo.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findById(UUID id);

    @Query(value = "SELECT id, big_image_url FROM book WHERE id NOT IN (SELECT DISTINCT book_id FROM book_image) LIMIT 30", nativeQuery = true)
    Set<Object[]> findBookWhereImageDoNotExist();
}
