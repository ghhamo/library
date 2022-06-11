package job.hamo.library.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "bookList")
public class BookList {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "list_books",
            joinColumns = {@JoinColumn(name = "list_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    private List<Book> bookEntities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return bookEntities;
    }

    public void setBooks(List<Book> bookEntities) {
        this.bookEntities = bookEntities;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
