package job.hamo.library.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@Component
public class BatchProcessor {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @PersistenceContext
    private EntityManager entityManager;

    public <T> void batchInsert(Collection<T> entities, JpaRepository<T, Long> repository) {
        int threadCount = (int) Math.ceil(entities.size() * 1.0 / batchSize);
        ForkJoinPool customThreadPool = new ForkJoinPool(threadCount);
        int i = 0;
        final List<T> resultList = new ArrayList<>(entities.size());
        List<T> currentList = new ArrayList<>(batchSize);
        List<ForkJoinTask<?>> tasks = new ArrayList<>(threadCount);
        for (T entity : entities) {
            currentList.add(entity);
            i++;
            if (i % batchSize == 0) {
                final List<T> list = currentList;
                tasks.add(customThreadPool.submit(() -> {
                    repository.saveAll(list);
                }));
                currentList = new ArrayList<>(batchSize);
            }
        }
        final List<T> list = currentList;
        tasks.add(customThreadPool.submit(() -> {
            repository.saveAll(list);
        }));
        for (ForkJoinTask<?> task : tasks) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
