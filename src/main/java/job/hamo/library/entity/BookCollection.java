package job.hamo.library.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class BookCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "collection_users",
            joinColumns = {@JoinColumn(name = "collection_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "collection_books",
            joinColumns = {@JoinColumn(name = "collection_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    private Set<Book> bookEntities;

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

    public Set<Book> getBooks() {
        return bookEntities;
    }

    public void setBooks(Set<Book> bookEntities) {
        this.bookEntities = bookEntities;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}

