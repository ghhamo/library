package job.hamo.library.dto;

import job.hamo.library.entity.Author;
import job.hamo.library.entity.Book;
import job.hamo.library.entity.Genre;

import java.util.UUID;

public record CreateBookDTO(UUID id, String title, String bigImageUrl, UUID authorId, UUID genreId) {

    public static CreateBookDTO fromBook(Book book) {
        return new CreateBookDTO(book.getId(), book.getTitle(),
                book.getBigImageUrl(), book.getAuthor().getId(), book.getGenre().getId());
    }

    public static Book toBook(CreateBookDTO createBookDTO) {
        Book book = new Book();
        book.setId(createBookDTO.id);
        book.setTitle(createBookDTO.title);
        book.setBigImageUrl(createBookDTO.bigImageUrl);
        Author author = new Author();
        author.setId(createBookDTO.authorId());
        book.setAuthor(author);
        Genre genre = new Genre();
        genre.setId(createBookDTO.genreId);
        book.setGenre(genre);
        return book;
    }
}
