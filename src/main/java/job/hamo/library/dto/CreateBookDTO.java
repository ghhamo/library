package job.hamo.library.dto;

import job.hamo.library.entity.Book;

public class CreateBookDTO {
    private final Long id;
    private final Long authorId;
    private final Long publisherId;
    private final Long genreId;
    private final String ISBN;
    private final String title;
    private final String yearOfPublication;

    public CreateBookDTO(Long id, Long authorId, Long publisherId, Long genreId, String isbn, String title, String yearOfPublication) {
        this.id = id;
        this.authorId = authorId;
        this.publisherId = publisherId;
        this.genreId = genreId;
        this.ISBN = isbn;
        this.title = title;
        this.yearOfPublication = yearOfPublication;
    }

    public static Book toBook(CreateBookDTO createBookDTO) {
        Book book = new Book();
        book.setId(createBookDTO.id);
        book.setTitle(createBookDTO.ISBN);
        book.setTitle(createBookDTO.title);
        book.setYearOfPublication(createBookDTO.yearOfPublication);
        return book;
    }

    public Long getId() {
        return id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public Long getGenreId() {
        return genreId;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public String getYearOfPublication() {
        return yearOfPublication;
    }
}
