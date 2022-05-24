package job.hamo.library.service;

import job.hamo.library.dto.CreateBookListDTO;
import job.hamo.library.dto.CreateListRelationshipDTO;
import job.hamo.library.entity.Book;
import job.hamo.library.entity.BookList;
import job.hamo.library.entity.User;
import job.hamo.library.exception.*;
import job.hamo.library.repository.BookListRepository;
import job.hamo.library.repository.BookRepository;
import job.hamo.library.dto.BookListDTO;
import job.hamo.library.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;

@Service
public class BookListService {

    @Autowired
    private BookListRepository bookListRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<CreateBookListDTO> importBookLists(Iterable<CreateBookListDTO> listDTOS) {
        List<CreateBookListDTO> invalidDTOs = new LinkedList<>();
        for (CreateBookListDTO bookListDTO : listDTOS) {
            if (bookListDTO == null) {
                continue;
            }
            try {
                create(bookListDTO);
            } catch (ValidationException validationException) {
                invalidDTOs.add(bookListDTO);
            }
        }
        return invalidDTOs;
    }

    @Transactional
    public List<CreateListRelationshipDTO> importBookListRelationships(Iterable<CreateListRelationshipDTO> relationshipDTOS) {
        List<CreateListRelationshipDTO> invalidDTOs = new LinkedList<>();
        for (CreateListRelationshipDTO relationshipDTO : relationshipDTOS) {
            if (relationshipDTO == null) {
                continue;
            }
            try {
                createRelationship(relationshipDTO);
            } catch (ValidationException validationException) {
                invalidDTOs.add(relationshipDTO);
            }
        }
        return invalidDTOs;
    }

    public Iterable<BookListDTO> getAllLists() {
        Iterable<BookList> lists = bookListRepository.findAll();
        return BookListDTO.mapBookListEntityListToBookListDtoList(lists);
    }

    public Iterable<BookListDTO> getListsWhereBookExist(Long id) {
        Objects.requireNonNull(id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookIdNotFoundException(id));
        Iterable<BookList> lists = bookListRepository.findAll();
        return BookListDTO.mapListEntityListToListDtoList(lists, book);
    }

    public BookListDTO getListById(Long id) {
        Objects.requireNonNull(id);
        BookList list = bookListRepository.findById(id).orElseThrow(() -> new BookListIdNotFoundException(id));
        return BookListDTO.fromBookList(list);
    }

    private void createRelationship(CreateListRelationshipDTO relationshipDTO) {
        Objects.requireNonNull(relationshipDTO);
        Objects.requireNonNull(relationshipDTO.bookId());
        Objects.requireNonNull(relationshipDTO.listId());
        BookList bookList = bookListRepository.findById(relationshipDTO.listId()).orElseThrow(() ->
                new BookListIdNotFoundException(relationshipDTO.listId()));
        Book book = bookRepository.findById(relationshipDTO.bookId()).orElseThrow(() ->
                new BookListIdNotFoundException(relationshipDTO.bookId()));
        entityManager.createNativeQuery("INSERT INTO list_books (list_id, book_id) VALUES(?,?)")
                .setParameter(1, bookList.getId())
                .setParameter(2, book.getId())
                .executeUpdate();
    }

    @Transactional
    public BookListDTO create(CreateBookListDTO bookListDto) {
        Objects.requireNonNull(bookListDto);
        Objects.requireNonNull(bookListDto.name());
        Objects.requireNonNull(bookListDto.userId());
        BookList bookList;
        User user = userRepository.findById(bookListDto.userId()).orElseThrow(() ->
                new UserIdNotFoundException(bookListDto.id()));
        if (bookListDto.id() != null) {
            boolean existsById = bookListRepository.existsById(bookListDto.id());
            if (existsById) {
                throw new BookListIdAlreadyExistsException(bookListDto.id());
            }
            entityManager.createNativeQuery("INSERT INTO book_list (id, name, user_id) VALUES (?,?,?)")
                    .setParameter(1, bookListDto.id())
                    .setParameter(2, bookListDto.name())
                    .setParameter(3, user.getId())
                    .executeUpdate();
            bookList = bookListRepository.getById(bookListDto.id());
        } else {
            bookList = bookListRepository.save(CreateBookListDTO.toBookCollection(bookListDto));
        }
        return BookListDTO.fromBookList(bookList);
    }

    public BookListDTO updateList(Long id) {
        Objects.requireNonNull(id);
        BookList bookList = bookListRepository.findById(id).orElseThrow(() -> new BookListIdNotFoundException(id));
        bookList.setName("bookList");
        bookListRepository.save(bookList);
        return BookListDTO.fromBookList(bookList);
    }

    public BookListDTO addBookToList(Long listId, Long bookId) {
        Objects.requireNonNull(listId);
        Objects.requireNonNull(bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookIdNotFoundException(bookId));
        BookList bookList = bookListRepository.findById(listId)
                .orElseThrow(() -> new BookListIdNotFoundException(listId));
        if (bookList.getBooks().contains(book)) {
            throw new BookIdAlreadyExistsException(bookId);
        } else {
            bookList.getBooks().add(book);
        }
        BookList savedList = bookListRepository.save(bookList);
        return BookListDTO.fromBookList(savedList);
    }

    public void deleteList(Long id) {
        Objects.requireNonNull(id);
        if (bookListRepository.existsById(id)) {
            bookListRepository.deleteById(id);
        } else throw new BookListIdNotFoundException(id);
    }
}
