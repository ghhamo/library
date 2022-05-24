package job.hamo.library.dto;

import job.hamo.library.entity.Author;

import java.util.HashSet;
import java.util.Set;

public record AuthorDTO(Long id, String name) {

    public static AuthorDTO fromAuthor(Author authorEntity) {
        return new AuthorDTO(authorEntity.getId(), authorEntity.getName());
    }

    public Author toAuthor() {
        Author authorEntity = new Author();
        authorEntity.setId(id);
        authorEntity.setName(name);
        return authorEntity;
    }

    public static Set<AuthorDTO> mapAuthorSetToAuthorDtoSet(Iterable<Author> authors) {
        Set<AuthorDTO> authorDTOSet = new HashSet<>();
        for (Author author : authors) {
            authorDTOSet.add(AuthorDTO.fromAuthor(author));
        }
        return authorDTOSet;

    }
}
