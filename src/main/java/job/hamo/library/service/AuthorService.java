package job.hamo.library.service;

import job.hamo.library.entity.Author;
import job.hamo.library.entity.Book;
import job.hamo.library.exception.AuthorUUIDAlreadyExistsException;
import job.hamo.library.exception.AuthorUUIDNotFoundException;
import job.hamo.library.exception.ValidationException;
import job.hamo.library.repository.AuthorRepository;
import job.hamo.library.dto.AuthorDTO;
import job.hamo.library.dto.BookDTO;
import job.hamo.library.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

import static job.hamo.library.util.DataGenerator.randomString;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<AuthorDTO> exportAll() {
        List<Author> all = authorRepository.findAll();
        List<AuthorDTO> result = new LinkedList<>();
        for (Author author : all) {
            result.add(AuthorDTO.fromAuthor(author));
        }
        return result;
    }

    @Transactional
    public List<AuthorDTO> importAuthors(Iterable<AuthorDTO> authorDTOS) {
        List<AuthorDTO> invalidDTOs = new LinkedList<>();
        for (AuthorDTO createAuthorDTO : authorDTOS) {
            if (createAuthorDTO == null) {
                continue;
            }
            try {
                create(createAuthorDTO);
            } catch (ValidationException validationException) {
                invalidDTOs.add(createAuthorDTO);
            }
        }
        return invalidDTOs;
    }

    public AuthorDTO getAuthorById(UUID id) {
        Objects.requireNonNull(id);
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorUUIDNotFoundException(id));
        return AuthorDTO.fromAuthor(author);

    }

    public void deleteAuthor(UUID id) {
        Objects.requireNonNull(id);
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
        } else throw new AuthorUUIDNotFoundException(id);
    }

    public Iterable<BookDTO> getBooksOfAuthor(UUID id) {
        Objects.requireNonNull(id);
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorUUIDNotFoundException(id));
        Iterable<Book> books = author.getBooks();
        return BookDTO.mapBookSetToBookDto(books);
    }

    public Iterable<AuthorDTO> getAllAuthor() {
        Iterable<Author> authors = authorRepository.findAll();
        return AuthorDTO.mapAuthorSetToAuthorDtoSet(authors);
    }

    @Transactional
    public AuthorDTO create(AuthorDTO authorDto) {
        Objects.requireNonNull(authorDto);
        Objects.requireNonNull(authorDto.name());
        Author author;
        if (authorDto.id() != null) {
            boolean existsById = authorRepository.existsById(authorDto.id());
            if (existsById) {
                throw new AuthorUUIDAlreadyExistsException(authorDto.id());
            }
            entityManager.createNativeQuery("INSERT INTO author (id, name) VALUES (?,?)")
                    .setParameter(1, UUIDUtil.asBytes(authorDto.id()))
                    .setParameter(2, authorDto.name())
                    .executeUpdate();
            author = authorRepository.getById(authorDto.id());
        } else {
            author = authorRepository.save(authorDto.toAuthor());
        }
        return AuthorDTO.fromAuthor(author);
    }

    public AuthorDTO updateAuthor(UUID id) {
        Objects.requireNonNull(id);
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorUUIDNotFoundException(id));
        author.setName(randomString(12));
        authorRepository.save(author);
        return AuthorDTO.fromAuthor(author);
    }

    public Author csvToAuthor(String[] authorRow) {
        Author author = new Author();
        author.setId(UUID.fromString(authorRow[0]));
        author.setName(authorRow[1]);
        return author;
    }
}
