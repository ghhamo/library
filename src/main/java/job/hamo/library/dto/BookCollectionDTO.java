package job.hamo.library.dto;

import job.hamo.library.entity.Book;
import job.hamo.library.entity.BookCollection;

import java.util.HashSet;
import java.util.Set;

public record BookCollectionDTO(Long id, String name) {

    public static BookCollectionDTO fromBookCollection(BookCollection bookCollectionEntity) {
        return new BookCollectionDTO(bookCollectionEntity.getId(), bookCollectionEntity.getName());
    }

    public static Iterable<BookCollectionDTO> mapCollectionListToCollectionDtoList(Iterable<BookCollection> collections) {
        Set<BookCollectionDTO> bookCollectionDTOSet = new HashSet<>();
        for (BookCollection bookCollection : collections) {
            bookCollectionDTOSet.add(BookCollectionDTO.fromBookCollection(bookCollection));
        }
        return bookCollectionDTOSet;
    }

    public static Iterable<BookCollectionDTO> mapCollectionEntityListToCollectionDtoList(Iterable<BookCollection> collections, Book book) {
        Set<BookCollectionDTO> bookCollectionDTOSet = new HashSet<>();
        for (BookCollection bookCollection : collections) {
            if (bookCollection.getBooks().contains(book)) {
                bookCollectionDTOSet.add(BookCollectionDTO.fromBookCollection(bookCollection));
            }
        }
        return bookCollectionDTOSet;
    }
}
