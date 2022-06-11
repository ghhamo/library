package job.hamo.library.util;

import job.hamo.library.service.BookAssetService;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageResizer {

    private final BookAssetService bookAssetService;

    @Autowired
    public ImageResizer(BookAssetService bookAssetService) {
        this.bookAssetService = bookAssetService;
    }

    @Value("${image.folder}")
    private String imageFolder;
    @Value("${image.medium.size}")
    private Integer imageMediumSize;
    @Value("${image.small.size}")
    private Integer imageSmallSize;

    @Transactional
    public void resizeImage(File sourceFile, Integer imageSize, Long bookId) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(sourceFile);
        BufferedImage outputImage = Scalr.resize(bufferedImage, imageSize);
        String newFileName = FilenameUtils.getBaseName(sourceFile.getName())
                + "_" + imageSize + "." + FilenameUtils.getExtension(sourceFile.getName());
        Path path = Paths.get(imageFolder, newFileName);
        File newImageFile = path.toFile();
        ImageIO.write(outputImage, "jpg", newImageFile);
        outputImage.flush();
        if (imageSize.equals(imageMediumSize)) {
            bookAssetService.updateBookAsset(imageFolder + newImageFile.getName(), bookId, "medium");
        } else if (imageSize.equals(imageSmallSize)) {
            bookAssetService.updateBookAsset(imageFolder + newImageFile.getName(), bookId, "small");
        }
    }
}

