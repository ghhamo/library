package job.hamo.library.util;

import job.hamo.library.entity.File;
import job.hamo.library.exception.BookImageUrlNotExistsException;
import job.hamo.library.repository.FileRepository;
import job.hamo.library.repository.BookRepository;
import job.hamo.library.service.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

@Component
public class ImageDownloader implements Runnable{

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private BookRepository bookRepository;

    @Value("${image.folder}")
    private String imageFolder;

    @Transactional
    public void downloadImage(Long bookId) throws IOException {
        Optional<String> bigImageUrlOfBook = bookRepository.findImageUrlLByBookId(bookId);
        if (bigImageUrlOfBook.isEmpty()) {
            throw new BookImageUrlNotExistsException();
        }
        URL url = new URL(bigImageUrlOfBook.get());
        InputStream in;
        try {
            in = new BufferedInputStream(url.openStream());
        } catch (ConnectException ignored) {
            return;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        int len;
        while (-1 != (len = in.read(buf))) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        String name = String.format("%s_%s", UUID.randomUUID().toString().substring(1, 13), System.currentTimeMillis() / 1000);
        java.io.File bigImage = new java.io.File(imageFolder + name + ".jpg");
        while (true) {
            if (bigImage.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(bigImage);
                fos.write(response);
                fos.close();
                break;
            } else {
                name = String.format("%s_%s", UUID.randomUUID().toString().substring(1, 13), System.currentTimeMillis() / 1000);
                bigImage = new java.io.File(imageFolder + name + ".jpg");
            }
        }
        File file = fileSystemService.createFileSystem(bigImage, bookId, "big");
        fileRepository.save(file);
    }

    @Override
    public void run() {

    }
}
