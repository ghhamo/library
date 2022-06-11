package job.hamo.library.util;

import job.hamo.library.entity.*;
import job.hamo.library.repository.BookAssetRepository;
import job.hamo.library.repository.BookRepository;
import job.hamo.library.service.BookAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class ScheduledTaskImpl {

    private final BookAssetService bookAssetService;
    private final BookAssetRepository bookAssetRepository;
    private final ImageDownloader imageDownloader;
    private final ImageResizer imageResizer;
    private final BookRepository bookRepository;

    @Autowired
    public ScheduledTaskImpl(BookAssetService bookAssetService, BookAssetRepository bookAssetRepository,
                             ImageDownloader imageDownloader, ImageResizer imageResizer,
                             BookRepository bookRepository) {
        this.bookAssetService = bookAssetService;
        this.bookAssetRepository = bookAssetRepository;
        this.imageDownloader = imageDownloader;
        this.imageResizer = imageResizer;
        this.bookRepository = bookRepository;
    }

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Value("${image.medium.size}")
    private Integer imageMediumSize;
    @Value("${image.small.size}")
    private Integer imageSmallSize;

    @Transactional
    public List<Long> getBookIdsWithPendingBigImages() {
        List<Long> bookIds = bookRepository.findBookIdsWithPendingBigImages(Status.PENDING.name());
        for (Long bookId : bookIds) {
            bookAssetService.insertOrUpdateBookAsset(bookId, "big", Status.IN_PROGRESS);
        }
        return bookIds;
    }

    public void downloadingImages(List<Long> bookIds) {
        for (Long id : bookIds) {
            threadPoolTaskExecutor.submit(() -> {
                try {
                    imageDownloader.downloadImage(id);
                } catch (IOException e) {
                    bookAssetService.updateBookAssetState(id, "big", Status.PENDING);
                    //todo: maybe count number of fails
                }
            });
        }
    }

    @Transactional
    public List<Long> getBookIdsWithPendingMediumOrSmallImages(String size) {
        List<Long> bookIds = bookRepository.findBookIdsWithPendingMediumOrSmallImages(Status.DONE.name(), size, Status.PENDING.name());
        for (Long bookId : bookIds) {
            bookAssetService.insertOrUpdateBookAsset(bookId, size, Status.IN_PROGRESS);
        }
        return bookIds;
    }

    public void resizingImages(List<Long> bookIds, String size) {
        for (Long bookId : bookIds) {
            Optional<String> path = bookAssetRepository.findBookImagePathByBookIdAndImageSize(bookId);
            if (path.isPresent()) {
                File file = new File(path.get());
                threadPoolTaskExecutor.submit(() -> {
                    try {
                        if (size.equals("medium")) {
                            imageResizer.resizeImage(file, imageMediumSize, bookId);
                        } else if (size.equals("small")) {
                            imageResizer.resizeImage(file, imageSmallSize, bookId);
                        } else {
                            throw new IllegalStateException();
                        }
                    } catch (IOException e) {
                        bookAssetService.updateBookAssetState(bookId, size, Status.PENDING);
                        //todo: maybe count number of fails
                    }
                });
            }
        }
    }

    @Transactional
    public void getInProgressImages() {
        List<Long> bookAssetIds = bookAssetRepository.findBookAssetIdsWithInProgressAndTime(Status.IN_PROGRESS.name());
        for (Long bookAssetId : bookAssetIds) {
            bookAssetRepository.updateFailedBookAsset(bookAssetId, Status.PENDING.name(), Status.IN_PROGRESS.name());
        }
    }
}