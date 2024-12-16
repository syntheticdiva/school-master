package school.service;

import jakarta.transaction.Transactional;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import school.dto.SubscriberDto;
import school.entity.SubscriberEntity;
import school.exception.SubscriberNotFoundException;
import school.exception.SubscriberServiceException;
import school.mapper.SubscriberMapper;
import school.repository.SubscriberRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final ThreadService threadService;
    private final SubscriberMapper subscriberMapper;

    @Autowired
    public SubscriberService(SubscriberRepository subscriberRepository,
                             ThreadService threadService,
                             SubscriberMapper subscriberMapper) {
        this.subscriberRepository = subscriberRepository;
        this.threadService = threadService;
        this.subscriberMapper = subscriberMapper;
    }
    private static final UrlValidator URL_VALIDATOR = new UrlValidator(
            new String[]{"http", "https"},
            UrlValidator.ALLOW_LOCAL_URLS
    );

    @Transactional
    public SubscriberEntity createSubscriber(SubscriberDto subscriberDto) {
        validateUrl(subscriberDto.getUrl());

        SubscriberEntity entity = new SubscriberEntity();
        entity.setEntity(
                Optional.ofNullable(subscriberDto.getEntity())
                        .orElse(SubscriberDto.ENTITY_SCHOOL)
        );
        entity.setEventType(
                Optional.ofNullable(subscriberDto.getEventType())
                        .orElse(SubscriberDto.EVENT_ON_CREATE)
        );
        entity.setUrl(subscriberDto.getUrl());
        entity.setCreatedAt(LocalDateTime.now());

        try {
            return subscriberRepository.save(entity);
        } catch (DataAccessException e) {
            throw new SubscriberServiceException(
                    "Error creating subscriber: invalid URL",
                    e
            );
        }
    }

    @Transactional
    public SubscriberEntity updateSubscriber(Long id, SubscriberDto subscriberDto) {
        SubscriberEntity existingEntity = subscriberRepository.findById(id)
                .orElseThrow(() -> new SubscriberNotFoundException("Subscriber not found with id: " + id));

        if (subscriberDto.getUrl() != null) {
            validateUrl(subscriberDto.getUrl());
        }

        existingEntity.setEntity(
                Optional.ofNullable(subscriberDto.getEntity())
                        .orElse(existingEntity.getEntity())
        );
        existingEntity.setEventType(
                Optional.ofNullable(subscriberDto.getEventType())
                        .orElse(existingEntity.getEventType())
        );
        existingEntity.setUrl(
                Optional.ofNullable(subscriberDto.getUrl())
                        .orElse(existingEntity.getUrl())
        );

        try {
            return subscriberRepository.save(existingEntity);
        } catch (DataAccessException e) {
            throw new SubscriberServiceException("Subscriber update error", e);
        }
    }
    @Transactional
    public void deleteSubscriber(Long id) {
        SubscriberEntity existingEntity = subscriberRepository.findById(id)
                .orElseThrow(() -> new SubscriberNotFoundException("Subscriber not found with id: " + id));

        try {
            subscriberRepository.delete(existingEntity);
            threadService.removeSubscriber(id);
        } catch (DataAccessException e) {
            throw new SubscriberServiceException("Error deleting subscriber due to data access issue", e);
        }
    }


    public List<SubscriberDto> getAllSubscribers() {
        List<SubscriberEntity> subscribers = subscriberRepository.findAll();
        return subscribers.stream()
                .map(subscriberMapper::toDto)
                .collect(Collectors.toList());
    }
    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        if (!URL_VALIDATOR.isValid(url)) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }

        try {
            URI uri = new URI(url);

            if (url.length() > 2000) {
                throw new IllegalArgumentException("URL is too long");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL parsing error: " + url, e);
        }
    }

}