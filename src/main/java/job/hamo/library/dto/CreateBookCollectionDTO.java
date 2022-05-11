package job.hamo.library.dto;

import job.hamo.library.entity.BookCollection;
import job.hamo.library.entity.User;

import java.util.UUID;

public record CreateBookCollectionDTO(UUID id, String name, UUID userId) {

    public static CreateBookCollectionDTO fromBookCollection(BookCollection bookCollection) {
        return new CreateBookCollectionDTO(bookCollection.getId(),
                bookCollection.getName(), bookCollection.getUser().getId());
    }

    public static BookCollection toBookCollection(CreateBookCollectionDTO createBookCollectionDTO) {
        BookCollection bookCollection = new BookCollection();
        bookCollection.setId(createBookCollectionDTO.id());
        bookCollection.setName(createBookCollectionDTO.name());
        User user = new User();
        user.setId(createBookCollectionDTO.userId());
        bookCollection.setUser(user);
        return bookCollection;
    }
}
