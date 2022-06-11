package job.hamo.library.dto;

import job.hamo.library.entity.Author;

import java.util.HashSet;
import java.util.Set;

public record AuthorDTO(Long id, String name) {

    public static AuthorDTO fromAuthor(Author author) {
        return new AuthorDTO(author.getId(), author.getName());
    }

    public Author toAuthor() {
        Author author = new Author();
        author.setId(id);
        author.setName(name);
        return author;
    }

    public static Set<AuthorDTO> mapAuthorSetToAuthorDtoSet(Iterable<Author> authors) {
        Set<AuthorDTO> authorDTOSet = new HashSet<>();
        for (Author author : authors) {
            authorDTOSet.add(AuthorDTO.fromAuthor(author));
        }
        return authorDTOSet;

    }
}
