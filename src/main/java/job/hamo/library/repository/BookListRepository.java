package job.hamo.library.repository;

import job.hamo.library.entity.BookList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface BookListRepository extends JpaRepository<BookList, UUID> {
    Optional<BookList> findById(UUID id);

    @Query(value = "SELECT * FROM list_books", nativeQuery = true)
    Set<Object[]> findAllRelationship();
}
