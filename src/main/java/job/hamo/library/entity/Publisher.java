package job.hamo.library.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class Publisher {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="publisher_id_sequence")
    @SequenceGenerator(name="publisher_id_sequence", sequenceName = "publisher_id_sequence", allocationSize = 10000)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Book> books;

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
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publisher publisher)) return false;
        return name.equalsIgnoreCase(publisher.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}
