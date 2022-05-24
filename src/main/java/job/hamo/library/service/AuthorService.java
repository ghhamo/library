package job.hamo.library.service;

import job.hamo.library.dto.AuthorDTO;
import job.hamo.library.dto.BookDTO;
import job.hamo.library.entity.Author;
import job.hamo.library.entity.Book;
import job.hamo.library.exception.AuthorIdAlreadyExistsException;
import job.hamo.library.exception.AuthorIdNotFoundException;
import job.hamo.library.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.*;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityManager entityManager;

    public Map<String, Author> authorsMapToMap(Collection<Author> list) {
        Map<String, Author> authors = new HashMap<>();
        for (Author author : list) {
            authors.put(author.getName().toLowerCase(), author);
        }
        return authors;
    }

    public AuthorDTO getAuthorById(Long id) {
        Objects.requireNonNull(id);
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorIdNotFoundException(id));
        return AuthorDTO.fromAuthor(author);

    }

    public void deleteAuthor(Long id) {
        Objects.requireNonNull(id);
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
        } else throw new AuthorIdNotFoundException(id);
    }

    public Iterable<BookDTO> getBooksOfAuthor(Long id) {
        Objects.requireNonNull(id);
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorIdNotFoundException(id));
        Iterable<Book> books = author.getBooks();
        return BookDTO.mapBookSetToBookDto(books);
    }

    public Iterable<AuthorDTO> getAllAuthor() {
        Iterable<Author> authors = authorRepository.findAll();
        return AuthorDTO.mapAuthorSetToAuthorDtoSet(authors);
    }

    public AuthorDTO create(AuthorDTO authorDto) {
        Objects.requireNonNull(authorDto);
        Objects.requireNonNull(authorDto.name());
        Author author;
        if (authorDto.id() != null) {
            boolean existsById = authorRepository.existsById(authorDto.id());
            if (existsById) {
                throw new AuthorIdAlreadyExistsException(authorDto.id());
            }
            entityManager.createNativeQuery("INSERT INTO author (id, name) VALUES (?,?)")
                    .setParameter(1, authorDto.id())
                    .setParameter(2, authorDto.name())
                    .executeUpdate();
            author = authorRepository.getById(authorDto.id());
        } else {
            author = authorRepository.save(authorDto.toAuthor());
        }
        return AuthorDTO.fromAuthor(author);
    }

    public AuthorDTO updateAuthor(Long id) {
        Objects.requireNonNull(id);
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorIdNotFoundException(id));
        author.setName("author");
        authorRepository.save(author);
        return AuthorDTO.fromAuthor(author);
    }
}
