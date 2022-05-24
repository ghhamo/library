package job.hamo.library.dto;

import job.hamo.library.entity.Book;

public class CreateBookDTO {
    private Long id;
    private final String ISBN;
    private final String title;
    private final Long authorId;
    private final String yearOfPublication;
    private final Long publisherId;
    private final String imageUrlS;
    private final String imageUrlM;
    private final String imageUrlL;

    public CreateBookDTO(Long id, String ISBN, String title, Long authorId, String yearOfPublication,
                         Long publisherId, String imageUrlS, String imageUrlM, String imageUrlL) {
        this.id = id;
        this.ISBN = ISBN;
        this.title = title;
        this.authorId = authorId;
        this.yearOfPublication = yearOfPublication;
        this.publisherId = publisherId;
        this.imageUrlS = imageUrlS;
        this.imageUrlM = imageUrlM;
        this.imageUrlL = imageUrlL;
    }

    public CreateBookDTO(String ISBN, String title, Long authorId, String yearOfPublication,
                         Long publication, String imageUrlS, String imageUrlM, String imageUrlL) {
        this.ISBN = ISBN;
        this.title = title;
        this.authorId = authorId;
        this.yearOfPublication = yearOfPublication;
        this.publisherId = publication;
        this.imageUrlS = imageUrlS;
        this.imageUrlM = imageUrlM;
        this.imageUrlL = imageUrlL;
    }

    public static CreateBookDTO fromBookWithId(Book book) {
        return new CreateBookDTO(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getYearOfPublication(),
                book.getPublisher().getId(),
                book.getImageUrlS(),
                book.getImageUrlM(),
                book.getImageUrlL());
    }

    public static CreateBookDTO fromBookWithoutId(Book book) {
        return new CreateBookDTO(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getYearOfPublication(),
                book.getPublisher().getId(),
                book.getImageUrlS(),
                book.getImageUrlM(),
                book.getImageUrlL());
    }

    public static Book toBook(CreateBookDTO createBookDTO) {
        Book book = new Book();
        book.setId(createBookDTO.id);
        book.setTitle(createBookDTO.ISBN);
        book.setTitle(createBookDTO.title);
        book.setYearOfPublication(createBookDTO.yearOfPublication);
        book.setImageUrlL(createBookDTO.imageUrlL);
        book.setImageUrlL(createBookDTO.imageUrlM);
        book.setImageUrlL(createBookDTO.imageUrlS);
        return book;
    }

    public Long getId() {
        return id;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getImageUrlL() {
        return imageUrlL;
    }

    public String getImageUrlM() {
        return imageUrlM;
    }

    public String getImageUrlS() {
        return imageUrlS;
    }

    public String getYearOfPublication() {
        return yearOfPublication;
    }

    public Long getPublisherId() {
        return publisherId;
    }
}
