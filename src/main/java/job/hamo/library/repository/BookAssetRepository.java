package job.hamo.library.repository;

import job.hamo.library.entity.BookAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookAssetRepository extends JpaRepository<BookAsset, Long> {
    Optional<BookAsset> findById(Long id);

    Optional<BookAsset> findByBookIdAndImageSize(Long bookId, String imageSize);

    @Query(value = "SELECT f.path FROM BookAsset AS f WHERE f.book.id = :id AND f.imageSize = 'big'")
    Optional<String> findBookImagePathByBookIdAndImageSize(@Param("id") Long id);

    @Query(value = "FROM BookAsset AS b_a WHERE b_a.book.id = :id AND b_a.imageSize = 'big' AND b_a.status = 'IN_PROGRESS'")
    BookAsset findBookAssetByBookIdImageBigSizeAndStatus(@Param("id") Long id);

    @Query(value = "FROM BookAsset AS b_a WHERE b_a.book.id = :id AND b_a.imageSize = 'medium' AND b_a.status = 'IN_PROGRESS'")
    BookAsset findBookAssetByBookIdImageMediumSizeAndStatus(@Param("id") Long id);

    @Query(value = "FROM BookAsset AS b_a WHERE b_a.book.id = :id AND b_a.imageSize = 'small' AND b_a.status = 'IN_PROGRESS'")
    BookAsset findBookAssetByBookIdImageSmallSizeAndStatus(@Param("id") Long id);

    @Query(value = "SELECT id FROM book_asset WHERE TIMEDIFF(NOW(), date) < '00:30' AND status =:in_progress FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<Long> findBookAssetIdsWithInProgressAndTime(@Param("in_progress") String in_progress);

    @Modifying
    @Query(value = "UPDATE book_asset SET status =:pending WHERE id =:book_id AND TIMEDIFF(NOW(), date) < '00:30' AND status =:in_progress", nativeQuery = true)
    void updateFailedBookAsset(@Param("book_id") Long bookId, @Param("pending") String pending, @Param("in_progress") String inProgress);
}
