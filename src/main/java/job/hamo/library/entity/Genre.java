package job.hamo.library.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "genre", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
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
}
