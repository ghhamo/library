package job.hamo.library.util;


import job.hamo.library.entity.File;
import job.hamo.library.repository.FileRepository;
import job.hamo.library.service.FileSystemService;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageResizer {

    @Value("${image.folder}")
    private String imageFolder;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private FileRepository fileRepository;

    @Value("${image.medium.size}")
    private Integer imageMediumSize;

    @Value("${image.small.size}")
    private Integer imageSmallSize;

    @Transactional
    public void resizeImage(java.io.File sourceFile, Integer imageSize, Long bookId) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(sourceFile);
        BufferedImage outputImage = Scalr.resize(bufferedImage, imageSize);
        String newFileName = FilenameUtils.getBaseName(sourceFile.getName())
                + "_" + imageSize + "." + FilenameUtils.getExtension(sourceFile.getName());
        Path path = Paths.get(imageFolder, newFileName);
        java.io.File newImageFile = path.toFile();
        ImageIO.write(outputImage, "jpg", newImageFile);
        outputImage.flush();
        File file = null;
        if (imageSize.equals(imageMediumSize)) {
            file = fileSystemService.createFileSystem(newImageFile, bookId, "medium");
        } else if (imageSize.equals(imageSmallSize)) {
            file = fileSystemService.createFileSystem(newImageFile, bookId, "small");
        }
        assert file != null;
        fileRepository.save(file);
    }
}

