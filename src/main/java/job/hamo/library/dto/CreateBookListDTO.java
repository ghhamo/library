package job.hamo.library.dto;

import job.hamo.library.entity.BookList;
import job.hamo.library.entity.User;

import java.util.UUID;

public record CreateBookListDTO(Long id, String name, Long userId) {

    public static CreateBookListDTO fromBookList(BookList bookList) {
        return new CreateBookListDTO(bookList.getId(),
                bookList.getName(), bookList.getUser().getId());
    }

    public static BookList toBookCollection(CreateBookListDTO createBookListDTO) {
        BookList bookList = new BookList();
        bookList.setId(createBookListDTO.id());
        bookList.setName(createBookListDTO.name());
        User user = new User();
        user.setId(createBookListDTO.userId());
        bookList.setUser(user);
        return bookList;
    }
}
