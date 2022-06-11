package job.hamo.library.repository;

import job.hamo.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findById(Long id);

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findAllByAuthorName(String name, Pageable pageable);

    Page<Book> findAllByAuthorId(Long id, Pageable pageable);

    Page<Book> findAllByPublisherName(String name, Pageable pageable);

    @Query(value = "SELECT book.isbn, book.title, author.name, publisher.name, book_asset.path, " +
            "rating.rating FROM book INNER JOIN author ON book.author_id = author.id INNER JOIN " +
            "publisher ON book.publisher_id = publisher.id INNER JOIN book_asset ON " +
            "book.id = book_asset.book_id INNER JOIN rating ON book.id = rating.book_id" +
            " ORDER BY book.isbn ASC LIMIT :limit", nativeQuery = true)
    List<Object[]> findAllBooksByAuthorAndPublisherIds(@Param("limit") int limit);

    @Query(value = "SELECT book.imageUrlL FROM Book as book WHERE book.id = :id")
    Optional<String> findImageUrlLByBookId(@Param("id") Long id);

    @Query(value = "FROM Book AS book WHERE book.id NOT IN (SELECT DISTINCT b_a.book.id FROM BookAsset AS b_a where b_a.imageSize = 'big')")
    Optional<Book> getBookWhereBigImageDoNotExistsById(Long id);

    @Query(value = "SELECT id FROM book WHERE book.id IN (SELECT DISTINCT book_id FROM book_asset WHERE status =:done) AND book.id NOT IN (SELECT DISTINCT book_id FROM book_asset WHERE image_size =:image_size AND status !=:pending) LIMIT 10 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<Long> findBookIdsWithPendingMediumOrSmallImages(@Param("done") String done, @Param("image_size") String image_size, @Param("pending") String pending);

    @Query(value = "SELECT id FROM book WHERE book.id NOT IN (SELECT DISTINCT book_id FROM book_asset WHERE image_size ='big' AND status !=:pending) LIMIT 10 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<Long> findBookIdsWithPendingBigImages(@Param("pending") String pending);
}
