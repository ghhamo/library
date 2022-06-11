package job.hamo.library.service;

import job.hamo.library.dto.PaginationDTO;
import job.hamo.library.dto.PublisherDTO;
import job.hamo.library.entity.Publisher;
import job.hamo.library.exception.BookListIdNotFoundException;
import job.hamo.library.exception.PublisherIdNotFoundException;
import job.hamo.library.exception.PublisherNameAlreadyExistsException;
import job.hamo.library.exception.PublisherNameNotFoundException;
import job.hamo.library.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;

    @Autowired
    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public Map<String, Publisher> publishersMapToMap(Collection<Publisher> list) {
        Map<String, Publisher> publishers = new HashMap<>();
        for (Publisher publisher : list) {
            publishers.put(publisher.getName().toLowerCase(), publisher);
        }
        return publishers;
    }

    public Iterable<PublisherDTO> getAllPublisher(PaginationDTO paginationDTO) {
            PageRequest pageRequest = PageRequest.of(paginationDTO.pageNumber(), paginationDTO.pageSize());
            Page<Publisher> publishers = publisherRepository.findAll(pageRequest);
        return PublisherDTO.mapPublisherListToPublisherDtoList(publishers);
    }

    public PublisherDTO create(PublisherDTO publisherDto) {
        Objects.requireNonNull(publisherDto);
        Objects.requireNonNull(publisherDto.name());
        Optional<Publisher> publisher = publisherRepository.findByName(publisherDto.name());
        if (publisher.isPresent()) {
            throw new PublisherNameAlreadyExistsException(publisherDto.name());
        }
        Publisher savedPublisher = publisherRepository.save(PublisherDTO.toPublisher(publisherDto));
        return PublisherDTO.fromPublisher(savedPublisher);
    }

    public PublisherDTO getPublisherById(Long id) {
        Objects.requireNonNull(id);
        Publisher publisher = publisherRepository.findById(id).orElseThrow(() -> new PublisherIdNotFoundException(id));
        return PublisherDTO.fromPublisher(publisher);
    }

    public PublisherDTO getPublisherByName(String name) {
        Objects.requireNonNull(name);
        Publisher publisher = publisherRepository.findByName(name).orElseThrow(() -> new PublisherNameNotFoundException(name));
        return PublisherDTO.fromPublisher(publisher);
    }

    public PublisherDTO updatePublisher(Long id) {
        Objects.requireNonNull(id);
        Publisher publisher = publisherRepository.findById(id).orElseThrow(() -> new PublisherIdNotFoundException(id));
        publisher.setName("publisher");
        Publisher changedPublisher = publisherRepository.save(publisher);
        return PublisherDTO.fromPublisher(changedPublisher);
    }

    public void deletePublisher(Long id) {
        Objects.requireNonNull(id);
        if (publisherRepository.existsById(id)) {
            publisherRepository.deleteById(id);
        } else throw new BookListIdNotFoundException(id);
    }
}
