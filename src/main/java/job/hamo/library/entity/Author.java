package job.hamo.library.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(indexes = @Index(columnList = "name"))
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="author_id_sequence")
    @SequenceGenerator(name="author_id_sequence", sequenceName = "author_id_sequence", allocationSize = 10000)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author author)) return false;
        return name.equalsIgnoreCase(author.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}
