package job.hamo.library.util;

import job.hamo.library.entity.BookImage;
import job.hamo.library.repository.BookImageRepository;
import job.hamo.library.service.BookImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ImageDownloader implements Runnable{

    @Autowired
    private ImageResizer imageResizer;

    @Autowired
    private BookImageService bookImageService;

    @Autowired
    private BookImageRepository bookImageRepository;

    @Value("${image.folder}")
    String imageFolder;

    @Value("${image.medium.size}")
    private Integer imageMediumSize;

    @Value("${image.small.size}")
    private Integer imageSmallSize;

    public void downloadImage(UUID bookId, String bigImageUrlOfBook) throws IOException {
        List<BookImage> bookImages = new ArrayList<>();
        URL url = new URL(bigImageUrlOfBook);
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
        File bigImage = new File(imageFolder + name + ".jpg");
        while (true) {
            if (bigImage.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(bigImage);
                fos.write(response);
                fos.close();
                break;
            } else {
                name = String.format("%s_%s", UUID.randomUUID().toString().substring(1, 13), System.currentTimeMillis() / 1000);
                bigImage = new File(imageFolder + name + ".jpg");
            }
        }
        BookImage bookImage = bookImageService.addBookImage(bigImage, bookId, "big");
        bookImages.add(bookImage);
        File mediumImage = imageResizer.resizeImage(bigImage, imageMediumSize);
        bookImage = bookImageService.addBookImage(mediumImage, bookId, "medium");
        bookImages.add(bookImage);
        File smallImage = imageResizer.resizeImage(bigImage, imageSmallSize);
        bookImage = bookImageService.addBookImage(smallImage, bookId, "small");
        bookImages.add(bookImage);
        bookImageRepository.saveAll(bookImages);
    }

    @Override
    public void run() {

    }
}
