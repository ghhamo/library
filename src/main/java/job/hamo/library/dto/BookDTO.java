package job.hamo.library.dto;

import job.hamo.library.entity.Book;

import java.util.HashSet;
import java.util.Set;

public record BookDTO(Long id, String title, int rating) {

    public static BookDTO fromBook(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getRating());
    }

    public static Set<BookDTO> mapBookSetToBookDto(Iterable<Book> books) {
        Set<BookDTO> bookDTOSet = new HashSet<>();
        for (Book book : books) {
            bookDTOSet.add(BookDTO.fromBook(book));
        }
        return bookDTOSet;
    }
}
