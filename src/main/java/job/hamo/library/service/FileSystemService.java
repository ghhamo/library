package job.hamo.library.service;

import job.hamo.library.entity.Book;
import job.hamo.library.entity.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileSystemService {

    @Value("${image.folder}")
    private String imageFolder;

    public File createFileSystem(java.io.File file, Long bookId, String imageSize) {
        File fileSystem = new File();
        fileSystem.setImageSize(imageSize);
        fileSystem.setPath(imageFolder + file.getName());
        Book book = new Book();
        book.setId(bookId);
        fileSystem.setBook(book);
        return fileSystem;
    }

}
