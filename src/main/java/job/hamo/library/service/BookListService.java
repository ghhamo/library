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
import job.hamo.library.util.CSVParser;
import job.hamo.library.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

import static job.hamo.library.util.DataGenerator.randomString;

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
    public List<CreateBookListDTO> exportAll() {
        List<BookList> all = bookListRepository.findAll();
        List<CreateBookListDTO> result = new LinkedList<>();
        for (BookList bookList : all) {
            result.add(CreateBookListDTO.fromBookList(bookList));
        }
        return result;
    }

    @Transactional
    public List<CreateListRelationshipDTO> exportAllRelationship() {
        Set<Object[]> relationships = bookListRepository.findAllRelationship();
        List<CreateListRelationshipDTO> result = new ArrayList<>();
        for (Object[] relationship : relationships) {
            result.add(CreateListRelationshipDTO.makeRelationship(relationship[0], relationship[1]));
        }
        return result;
    }

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

    public Iterable<BookListDTO> getListsWhereBookExist(UUID id) {
        Objects.requireNonNull(id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookUUIDNotFoundException(id));
        Iterable<BookList> lists = bookListRepository.findAll();
        return BookListDTO.mapListEntityListToListDtoList(lists, book);
    }

    public BookListDTO getListById(UUID id) {
        Objects.requireNonNull(id);
        BookList list = bookListRepository.findById(id).orElseThrow(() -> new BookListUUIDNotFoundException(id));
        return BookListDTO.fromBookList(list);
    }

    private void createRelationship(CreateListRelationshipDTO relationshipDTO) {
        Objects.requireNonNull(relationshipDTO);
        Objects.requireNonNull(relationshipDTO.bookId());
        Objects.requireNonNull(relationshipDTO.listId());
        BookList bookList = bookListRepository.findById(relationshipDTO.listId()).orElseThrow(() ->
                new BookListUUIDNotFoundException(relationshipDTO.listId()));
        Book book = bookRepository.findById(relationshipDTO.bookId()).orElseThrow(() ->
                new BookListUUIDNotFoundException(relationshipDTO.bookId()));
        entityManager.createNativeQuery("INSERT INTO list_books (list_id, book_id) VALUES(?,?)")
                .setParameter(1, UUIDUtil.asBytes(bookList.getId()))
                .setParameter(2, UUIDUtil.asBytes(book.getId()))
                .executeUpdate();
    }

    public BookListDTO create(CreateBookListDTO bookListDto) {
        Objects.requireNonNull(bookListDto);
        Objects.requireNonNull(bookListDto.name());
        Objects.requireNonNull(bookListDto.userId());
        BookList bookList;
        User user = userRepository.findById(bookListDto.userId()).orElseThrow(() ->
                new UserUUIDNotFoundException(bookListDto.id()));
        if (bookListDto.id() != null) {
            boolean existsById = bookListRepository.existsById(bookListDto.id());
            if (existsById) {
                throw new BookListUUIDAlreadyExistsException(bookListDto.id());
            }
            entityManager.createNativeQuery("INSERT INTO book_list (id, name, user_id) VALUES (?,?,?)")
                    .setParameter(1, UUIDUtil.asBytes(bookListDto.id()))
                    .setParameter(2, bookListDto.name())
                    .setParameter(3, UUIDUtil.asBytes(user.getId()))
                    .executeUpdate();
            bookList = bookListRepository.getById(bookListDto.id());
        } else {
            bookList = bookListRepository.save(CreateBookListDTO.toBookCollection(bookListDto));
        }
        return BookListDTO.fromBookList(bookList);
    }

    public BookListDTO updateList(UUID id) {
        Objects.requireNonNull(id);
        BookList bookList = bookListRepository.findById(id).orElseThrow(() -> new BookListUUIDNotFoundException(id));
        bookList.setName(randomString(12));
        bookListRepository.save(bookList);
        return BookListDTO.fromBookList(bookList);
    }

    public BookListDTO addBookToList(UUID listId, UUID bookId) {
        Objects.requireNonNull(listId);
        Objects.requireNonNull(bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookUUIDNotFoundException(bookId));
        BookList bookList = bookListRepository.findById(listId)
                .orElseThrow(() -> new BookListUUIDNotFoundException(listId));
        if (bookList.getBooks().contains(book)) {
            throw new BookUUIDAlreadyExistsException(bookId);
        } else {
            bookList.getBooks().add(book);
        }
        BookList savedList = bookListRepository.save(bookList);
        return BookListDTO.fromBookList(savedList);
    }

    public void deleteList(UUID id) {
        Objects.requireNonNull(id);
        if (bookListRepository.existsById(id)) {
            bookListRepository.deleteById(id);
        } else throw new BookListUUIDNotFoundException(id);
    }

    public BookList csvToBookList(String[] bookListRow) {
        BookList bookList = new BookList();
        bookList.setId(UUID.fromString(bookListRow[0]));
        bookList.setName(bookListRow[1]);
        User user = new User();
        user.setId(UUID.fromString(bookListRow[2]));
        bookList.setUser(user);
        return bookList;
    }
}
