package job.hamo.library.dto;

import job.hamo.library.entity.Book;
import job.hamo.library.entity.BookList;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record BookListDTO(UUID id, String name) {

    public static BookListDTO fromBookList(BookList bookList) {
       return new BookListDTO(bookList.getId(), bookList.getName());

    }

    public static BookList toBookList(BookListDTO bookListDto) {
        BookList bookList = new BookList();
        bookList.setName(bookListDto.name);
        return bookList;
    }

    public static Iterable<BookListDTO> mapBookListEntityListToBookListDtoList(Iterable<BookList> lists) {
        Set<BookListDTO> listDtoSet = new HashSet<>();
        for (BookList bookList : lists) {
            listDtoSet.add(BookListDTO.fromBookList(bookList));
        }
        return listDtoSet;
    }

    public static Iterable<BookListDTO> mapListEntityListToListDtoList(Iterable<BookList> lists, Book book) {
        Set<BookListDTO> listDtoSet = new HashSet<>();
        for (BookList bookList : lists) {
            if (bookList.getBooks().contains(book)) {
                listDtoSet.add(BookListDTO.fromBookList(bookList));
            }
        }
        return listDtoSet;
    }
}
