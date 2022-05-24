package job.hamo.library.repository;

import job.hamo.library.entity.BookCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface BookCollectionRepository extends JpaRepository<BookCollection, Long> {
    Optional<BookCollection> findById(Long id);

    @Query(value = "SELECT * FROM collection_books", nativeQuery = true)
    Set<Object[]> findAllRelationship();
}
