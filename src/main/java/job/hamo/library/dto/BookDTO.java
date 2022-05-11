package job.hamo.library.dto;

import job.hamo.library.entity.Book;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record BookDTO(UUID id, String title, int rating) {

    public static BookDTO fromBook(Book bookEntity) {
        return new BookDTO(bookEntity.getId(), bookEntity.getTitle(), bookEntity.getRating());
    }

    public static Book toBook(BookDTO bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.title);
        book.setRating(bookDto.rating);
        return book;
    }

    public static Set<BookDTO> mapBookSetToBookDto(Iterable<Book> books) {
        Set<BookDTO> bookDTOSet = new HashSet<>();
        for (Book book : books) {
            bookDTOSet.add(BookDTO.fromBook(book));
        }
        return bookDTOSet;
    }
}
