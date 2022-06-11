package job.hamo.library.service;

import job.hamo.library.dto.AuthorDTO;
import job.hamo.library.dto.BookDTO;
import job.hamo.library.dto.PaginationDTO;
import job.hamo.library.entity.Author;
import job.hamo.library.entity.Book;
import job.hamo.library.exception.*;
import job.hamo.library.repository.AuthorRepository;
import job.hamo.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public Iterable<AuthorDTO> getAllAuthor(PaginationDTO paginationDTO) {
        PageRequest pageRequest = PageRequest.of(paginationDTO.pageNumber(), paginationDTO.pageSize());
        Page<Author> authors = authorRepository.findAll(pageRequest);
        return AuthorDTO.mapAuthorSetToAuthorDtoSet(authors);
    }

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

    public AuthorDTO getAuthorByName(String name) {
        Objects.requireNonNull(name);
        Author author = authorRepository.findByName(name).orElseThrow(() -> new AuthorNameNotFoundException(name));
        return AuthorDTO.fromAuthor(author);
    }

    public Iterable<BookDTO> getBooksOfAuthorByAuthorId(Long id, PaginationDTO paginationDTO) {
        Objects.requireNonNull(id);
        authorRepository.findById(id).orElseThrow(() -> new AuthorIdNotFoundException(id));
        PageRequest pageRequest = PageRequest.of(paginationDTO.pageNumber(), paginationDTO.pageSize());
        Page<Book> books = bookRepository.findAllByAuthorId(id, pageRequest);
        return BookDTO.mapBookSetToBookDto(books);
    }

    public AuthorDTO create(AuthorDTO authorDto) {
        Objects.requireNonNull(authorDto);
        Objects.requireNonNull(authorDto.name());
        Optional<Author> author = authorRepository.findByName(authorDto.name());
        if (author.isPresent()) {
            throw new AuthorNameAlreadyExistsException(authorDto.name());
        }
        Author savedAuthor = authorRepository.save(authorDto.toAuthor());
        return AuthorDTO.fromAuthor(savedAuthor);
    }

    public AuthorDTO updateAuthor(Long id) {
        Objects.requireNonNull(id);
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorIdNotFoundException(id));
        author.setName("author");
        authorRepository.save(author);
        return AuthorDTO.fromAuthor(author);
    }
}
