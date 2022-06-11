package job.hamo.library.service;

import job.hamo.library.dto.CreateBookListDTO;
import job.hamo.library.dto.PaginationDTO;
import job.hamo.library.entity.Book;
import job.hamo.library.entity.BookList;
import job.hamo.library.entity.User;
import job.hamo.library.exception.*;
import job.hamo.library.repository.BookListRepository;
import job.hamo.library.repository.BookRepository;
import job.hamo.library.dto.BookListDTO;
import job.hamo.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookListService {

    private final BookListRepository bookListRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookListService(BookListRepository bookListRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.bookListRepository = bookListRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Iterable<BookListDTO> getAllLists(PaginationDTO paginationDTO) {
        PageRequest pageRequest = PageRequest.of(paginationDTO.pageNumber(), paginationDTO.pageSize());
        Page<BookList> bookLists = bookListRepository.findAll(pageRequest);
        return BookListDTO.mapBookListEntityListToBookListDtoList(bookLists);
    }

    public Iterable<BookListDTO> getListsWhereBookExist(Long id) {
        Objects.requireNonNull(id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookIdNotFoundException(id));
        Iterable<BookList> lists = bookListRepository.findAll();
        return BookListDTO.mapListEntityListToListDtoList(lists, book);
    }

    public BookListDTO getListByName(String name) {
        Objects.requireNonNull(name);
        BookList bookList = bookListRepository.findByName(name).orElseThrow(() -> new BookListNameNotFoundException(name));
        return BookListDTO.fromBookList(bookList);
    }

    public BookListDTO getListById(Long id) {
        Objects.requireNonNull(id);
        BookList list = bookListRepository.findById(id).orElseThrow(() -> new BookListIdNotFoundException(id));
        return BookListDTO.fromBookList(list);
    }

    public BookListDTO create(CreateBookListDTO bookListDto, Long userId) {
        Objects.requireNonNull(bookListDto);
        Objects.requireNonNull(bookListDto.name());
        Objects.requireNonNull(userId);
        Optional<BookList> bookListFromDB = bookListRepository.findByName(bookListDto.name());
        if (bookListFromDB.isPresent()) {
            throw new BookListNameAlreadyExistsException(bookListDto.name());
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserIdNotFoundException(bookListDto.id()));
        BookList bookList = CreateBookListDTO.toBookList(bookListDto);
        bookList.setUser(user);
        BookList savedBookList = bookListRepository.save(bookList);
        return BookListDTO.fromBookList(savedBookList);
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
