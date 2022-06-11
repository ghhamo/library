package job.hamo.library.dto;

import job.hamo.library.entity.BookList;

public record CreateBookListDTO(Long id, String name) {

    public static BookList toBookList(CreateBookListDTO createBookListDTO) {
        BookList bookList = new BookList();
        bookList.setId(createBookListDTO.id());
        bookList.setName(createBookListDTO.name());
        return bookList;
    }
}
