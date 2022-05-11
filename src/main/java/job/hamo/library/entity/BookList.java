package job.hamo.library.entity;


import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookList")
public class BookList {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "list_books",
            joinColumns = {@JoinColumn(name = "list_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    private List<Book> books;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "BookList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user=" + user +
                ", books=" + books +
                '}';
    }
}
