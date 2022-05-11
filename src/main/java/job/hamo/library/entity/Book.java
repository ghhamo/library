package job.hamo.library.entity;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
public class Book {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String title;

    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<BookCollection> bookCollections;

    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<BookList> bookLists;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Rating> userRatings;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<BookImage> bookImages;

    @Column(columnDefinition = "integer default 0")
    private int rating;

    private Long countOfRating;

    private String bigImageUrl;

    private String mediumImageUrl;

    private String smallImageUrl;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Set<BookImage> getBookImages() {
        return bookImages;
    }

    public void setBookImages(Set<BookImage> bookImages) {
        this.bookImages = bookImages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<BookCollection> getCollections() {
        return bookCollections;
    }

    public void setCollections(Set<BookCollection> bookCollections) {
        this.bookCollections = bookCollections;
    }

    public Set<BookList> getBookLists() {
        return bookLists;
    }

    public void setBookLists(Set<BookList> bookLists) {
        this.bookLists = bookLists;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Set<Rating> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(Set<Rating> userRatings) {
        this.userRatings = userRatings;
    }

    public Long getCountOfRating() {
        return countOfRating;
    }

    public void setCountOfRating(Long countOfRating) {
        this.countOfRating = countOfRating;
    }

    public String getBigImageUrl() {
        return bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
        this.bigImageUrl = bigImageUrl;
    }

    public String getMediumImageUrl() {
        return mediumImageUrl;
    }

    public void setMediumImageUrl(String mediumImageUrl) {
        this.mediumImageUrl = mediumImageUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    public Set<BookImage> getLocalFiles() {
        return bookImages;
    }

    public void setLocalFiles(Set<BookImage> bookImages) {
        this.bookImages = bookImages;
    }
}
