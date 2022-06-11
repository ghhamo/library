package job.hamo.library.entity;

import javax.persistence.*;

@Entity
public class Rating {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rating_id_sequence")
    @SequenceGenerator(name = "rating_id_sequence", sequenceName = "rating_id_sequence", allocationSize = 50000)
    private Long id;

    private int rating;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
