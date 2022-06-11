package job.hamo.library.dto;

import job.hamo.library.entity.Book;
import job.hamo.library.entity.BookList;

import java.util.HashSet;
import java.util.Set;

public record BookListDTO(Long id, String name) {

    public static BookListDTO fromBookList(BookList bookList) {
       return new BookListDTO(bookList.getId(), bookList.getName());

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
