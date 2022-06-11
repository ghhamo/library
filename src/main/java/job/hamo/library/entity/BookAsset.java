package job.hamo.library.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"book_id", "image_size"})
)
public class BookAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String path;

    private String status;

    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false, name = "image_size")
    private String imageSize;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
