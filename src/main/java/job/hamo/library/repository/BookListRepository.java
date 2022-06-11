package job.hamo.library.repository;

import job.hamo.library.entity.BookList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookListRepository extends JpaRepository<BookList, Long> {
    Optional<BookList> findById(Long id);

    Optional<BookList> findByName(String name);

}
