package job.hamo.library.repository;

import job.hamo.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findById(Long id);

    Optional<Book> findByIsbn(String isbn);

    @Query(value = "FROM Book AS book WHERE book.id NOT IN (SELECT DISTINCT f.book.id FROM File AS f where f.imageSize = 'big')")
    Page<Book> findBookWhereBigImageDoNotExists(Pageable pageable);

    @Query(value = "FROM Book AS book WHERE book.id NOT IN (SELECT DISTINCT f.book.id FROM File AS f WHERE f.imageSize = 'medium')")
    Page<Book> findBookWhereMediumImageDoNotExists(Pageable pageable);

    @Query(value = "FROM Book AS book WHERE book.id NOT IN (SELECT DISTINCT f.book.id FROM File AS f WHERE f.imageSize = 'small')")
    Page<Book> findBookWhereSmallImageDoNotExists(Pageable pageable);

    @Query(value = "SELECT book.imageUrlL FROM Book as book WHERE book.id = :id")
    Optional<String> findImageUrlLByBookId(@Param("id") Long id);

    @Query(value = "FROM Book AS book WHERE book.id NOT IN (SELECT DISTINCT f.book.id FROM File AS f where f.imageSize = 'big')")
    Optional<Book> getBookWhereBigImageDoNotExistsById(Long id);
}
