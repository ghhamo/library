package job.hamo.library.util;

import job.hamo.library.dto.CreateBookDTO;
import job.hamo.library.dto.CreateUserDTO;
import job.hamo.library.entity.*;
import job.hamo.library.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CSVUtil {

    @Autowired
    private RoleRepository roleRepository;

    public BookCollection csvToCollection(String[] collectionRow) {
        BookCollection bookCollection = new BookCollection();
        bookCollection.setId(Long.parseLong(collectionRow[0]));
        bookCollection.setName(collectionRow[1]);
        User user = new User();
        user.setId(Long.parseLong(collectionRow[2]));
        bookCollection.setUser(user);
        return bookCollection;
    }

    public BookList csvToBookList(String[] bookListRow) {
        BookList bookList = new BookList();
        bookList.setId(Long.parseLong(bookListRow[0]));
        bookList.setName(bookListRow[1]);
        User user = new User();
        user.setId(Long.parseLong(bookListRow[2]));
        bookList.setUser(user);
        return bookList;
    }

    public Genre csvToGenre(String[] genreRow) {
        Genre genre = new Genre();
        genre.setId(Long.parseLong(genreRow[0]));
        genre.setName(genreRow[1]);
        return genre;
    }

    public Rating csvToRating(String[] ratingRow) {
        Rating rating = new Rating();
        rating.setId(Long.parseLong(ratingRow[0]));
        rating.setRating(Integer.parseInt(ratingRow[1]));
        Book book = new Book();
        book.setId(Long.parseLong(ratingRow[2]));
        rating.setBook(book);
        User user = new User();
        user.setId(Long.parseLong(ratingRow[3]));
        rating.setUser(user);
        return rating;
    }

    public Role csvToRole(String[] roleRow) {
        Role role = new Role();
        role.setId(Long.parseLong(roleRow[0]));
        role.setName(roleRow[1]);
        return role;
    }
}
