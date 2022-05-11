package job.hamo.library.service;

import job.hamo.library.entity.Book;
import job.hamo.library.entity.BookImage;
import job.hamo.library.repository.BookImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class BookImageService {

    public BookImage addBookImage(File file, UUID bookId, String imageSize) {
        BookImage bookImage = new BookImage();
        bookImage.setImageSize(imageSize);
        bookImage.setName(file.getName());
        Book book = new Book();
        book.setId(bookId);
        bookImage.setBook(book);
        return bookImage;
    }

}
