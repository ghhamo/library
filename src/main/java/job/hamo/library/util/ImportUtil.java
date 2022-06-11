package job.hamo.library.util;

import job.hamo.library.SetupDataLoader;
import job.hamo.library.dto.LocationDTO;
import job.hamo.library.entity.*;
import job.hamo.library.repository.*;
import job.hamo.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.*;

@Component
public class ImportUtil {

    private final RoleRepository roleRepository;
    private final PublisherService publisherService;
    private final CsvParser csvParser;
    private final AuthorService authorService;
    private final BookService bookService;
    private final LocationService locationService;
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public ImportUtil(RoleRepository roleRepository, PublisherService publisherService,
                      CsvParser csvParser, AuthorService authorService, BookService bookService,
                      LocationService locationService, EntityManagerFactory entityManagerFactory) {
        this.roleRepository = roleRepository;
        this.publisherService = publisherService;
        this.csvParser = csvParser;
        this.authorService = authorService;
        this.bookService = bookService;
        this.locationService = locationService;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @PersistenceContext
    private EntityManager entityManager;

    public void importAll(MultipartFile bookCsv, MultipartFile userCsv, MultipartFile ratingCsv) {
        List<String[]> rows = csvParser.csvParseToString(bookCsv, "book");
        Set<Author> setAuthorEntities = new HashSet<>();
        Set<Publisher> setPublishers = new HashSet<>();
        for (String[] row : rows) {
            Author author = new Author();
            author.setName(row[2]);
            setAuthorEntities.add(author);
            Publisher publisher = new Publisher();
            publisher.setName(row[4]);
            setPublishers.add(publisher);
        }
        List<Publisher> savedPublishers = batchInsert(setPublishers);
        List<Author> savedAuthorEntities = batchInsert(setAuthorEntities);

        Map<String, Publisher> nameToPublisher = publisherService.publishersMapToMap(savedPublishers);
        Map<String, Author> nameToAuthor = authorService.authorsMapToMap(savedAuthorEntities);
        Set<Book> bookSet = new HashSet<>();
        for (String[] row : rows) {
            Book book = new Book();
            book.setIsbn(row[0]);
            book.setTitle(row[1]);
            book.setAuthor(nameToAuthor.get(row[2].toLowerCase()));
            book.setYearOfPublication(row[3]);
            book.setPublisher(nameToPublisher.get(row[4].toLowerCase()));
            book.setImageUrlS(row[5]);
            book.setImageUrlM(row[6]);
            book.setImageUrlL(row[7]);
            bookSet.add(book);
        }
        List<Book> savedBookEntities = batchInsert(bookSet);
        Map<String, Book> isbnToBook = bookService.booksMapToMap(savedBookEntities);

        rows = csvParser.csvParseToString(userCsv, "user");
        List<User> userSet = new ArrayList<>();
        Role userRole = roleRepository.getByName(SetupDataLoader.USER);
        for (String[] row : rows) {
            String[] location = row[1].split(",");
            LocationDTO locationDTO = locationService.stringToLocation(location);
            User user = new User();
            user.setId(Long.parseLong(row[0]));
            user.setCity(locationDTO.city());
            user.setCountry(locationDTO.country());
            user.setRegion(locationDTO.region());
            user.setRole(userRole);
            user.setEnabled(true);
            if (row[2].equals("NULL")) {
                user.setAge(0);
            } else {
                user.setAge(Integer.parseInt(row[2]));
            }
            userSet.add(user);
        }
        Map<Long, User> users = saveAllAndGetOldIds(userSet);

        rows = csvParser.csvParseToString(ratingCsv, "rating");
        List<Rating> ratings = new ArrayList<>();
        for (String[] row : rows) {
            Long userId = Long.parseLong(row[0]);
            if (!(users.containsKey(userId) && isbnToBook.containsKey(row[1].toLowerCase()))) {
                continue;
            }
            Rating rating = new Rating();
            rating.setUser(users.get(userId));
            rating.setBook(isbnToBook.get(row[1].toLowerCase()));
            rating.setRating(Integer.parseInt(row[2]));
            ratings.add(rating);
        }
        batchInsert(ratings);
    }

    public Map<Long, User> saveAllAndGetOldIds(Iterable<User> entities) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager
                .getTransaction();
        Map<Long, User> result = new HashMap<>();
        try {
            entityTransaction.begin();
            entityManager.createNativeQuery("SET session unique_checks=0").executeUpdate();
            entityManager.createNativeQuery("SET session foreign_key_checks=0").executeUpdate();
            Assert.notNull(entities, "Entities must not be null!");
            for (User entity : entities) {
                long key = entity.getId();
                entity.setId(null);
                entityManager.persist(entity);
                result.put(key, entity);
            }
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
        return result;
    }

    private <T> List<T> saveAllBatchInNewTransaction(Iterable<T> entities) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        ArrayList<T> result = new ArrayList<>();
        try {
            entityTransaction.begin();
            entityManager.createNativeQuery("SET session unique_checks=0").executeUpdate();
            entityManager.createNativeQuery("SET session foreign_key_checks=0").executeUpdate();
            int i = 0;
            for (T entity : entities) {
                i++;
                entityManager.persist(entity);
                result.add(entity);
                if (i % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
        return result;
    }

    @Transactional
    public <T> List<T> batchInsert(Collection<T> entities) {
        int taskCount = (int) Math.ceil(entities.size() * 1.0 / batchSize);
        int i = 0;
        final List<T> resultList = new ArrayList<>(entities.size());
        List<T> currentList = new ArrayList<>(batchSize);
        List<Future<List<T>>> tasks = new ArrayList<>(taskCount);
        for (T entity : entities) {
            currentList.add(entity);
            i++;
            if (i % batchSize == 0) {
                final List<T> list = currentList;
                currentList = new ArrayList<>(batchSize);
                Future<List<T>> future = threadPoolTaskExecutor.submit(() -> saveAllBatchInNewTransaction(list));
                tasks.add(future);
            }
        }
        final List<T> list = currentList;
        Future<List<T>> future = threadPoolTaskExecutor.submit(() -> saveAllBatchInNewTransaction(list));
        tasks.add(future);
        for (Future<List<T>> task : tasks) {
            try {
                resultList.addAll(task.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        return resultList;
    }
}
