package job.hamo.library.dto;

import job.hamo.library.entity.BookCollection;

public record CreateBookCollectionDTO(Long id, String name) {

    public static CreateBookCollectionDTO fromBookCollection(BookCollection bookCollection) {
        return new CreateBookCollectionDTO(bookCollection.getId(),
                bookCollection.getName());
    }

    public static BookCollection toBookCollection(CreateBookCollectionDTO createBookCollectionDTO) {
        BookCollection bookCollection = new BookCollection();
        bookCollection.setId(createBookCollectionDTO.id());
        bookCollection.setName(createBookCollectionDTO.name());
        return bookCollection;
    }
}
