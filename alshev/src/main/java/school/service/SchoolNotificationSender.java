package school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.dto.*;
import school.mapper.SubscriberMapper;
import school.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.util.List;


//todo: написать самой на url из subscriberDto отправить schoolUpdateDto
@Slf4j
@Service
public class SchoolNotificationSender {

    private final RestTemplate restTemplate;
    private final SubscriberRepository subscriberRepository;
    private final SubscriberMapper subscriberMapper;
    private final ThreadService threadService;


    @Autowired
    public SchoolNotificationSender(
            RestTemplate restTemplate,
            SubscriberRepository subscriberRepository,
            SubscriberMapper subscriberMapper,
            ThreadService threadService
    ) {
        this.restTemplate = restTemplate;
        this.subscriberRepository = subscriberRepository;
        this.subscriberMapper = subscriberMapper;
        this.threadService = threadService;
    }

    public void sendCreate(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        log.info("Sending creation to {} message: {}", subscriberDto.getUrl(), schoolEntityDTO);

        SchoolOnCreateDto createDto = new SchoolOnCreateDto(
                schoolEntityDTO.getId(),
                schoolEntityDTO.getName(),
                schoolEntityDTO.getAddress(),
                LocalDateTime.now()
        );

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    subscriberDto.getUrl(),
                    createDto,
                    String.class
            );

            log.info("Creation notification sent successfully to {}. Response: {}",
                    subscriberDto.getUrl(), response);
        } catch (RestClientException e) {
            log.error("Failed to send creation notification to {}", subscriberDto.getUrl(), e);
        }
    }

    public void sendUpdate(SchoolUpdateDto schoolUpdateDto, SubscriberDto subscriberDto) {
        log.info("Sending update to {} message: {}", subscriberDto.getUrl(), schoolUpdateDto);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    subscriberDto.getUrl(),
                    schoolUpdateDto,
                    String.class
            );

            log.info("Update notification sent successfully to {}. Response: {}",
                    subscriberDto.getUrl(), response);
        } catch (RestClientException e) {
            log.error("Failed to send update notification to {}", subscriberDto.getUrl(), e);
        }
    }

    public void sendDelete(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        log.info("Sending deletion to {} message: {}", subscriberDto.getUrl(), schoolEntityDTO);

        SchoolOnDeleteDto deleteDto = new SchoolOnDeleteDto(
                schoolEntityDTO.getId(),
                schoolEntityDTO.getName(),
                schoolEntityDTO.getAddress()
        );

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    subscriberDto.getUrl(),
                    deleteDto,
                    String.class
            );

            log.info("Deletion notification sent successfully to {}. Response: {}",
                    subscriberDto.getUrl(), response);
        } catch (RestClientException e) {
            log.error("Failed to send deletion notification to {}", subscriberDto.getUrl(), e);
        }
    }
    public void notifySubscribers(String eventType, Object payload) {
        List<SubscriberDto> subscribers = threadService.getSubscribers();
        log.info("Notifying subscribers for event type: " + eventType + " with payload: " + payload);

        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(eventType) &&
                    subscriber.getEntity().equals(SubscriberDto.ENTITY_SCHOOL)) {

                switch (eventType) {
                    case SubscriberDto.EVENT_ON_CREATE:
                        log.info("Sending create notification to: " + subscriber.getUrl());
                        sendCreate((SchoolEntityDTO) payload, subscriber);
                        break;
                    case SubscriberDto.EVENT_ON_UPDATE:
                        log.info("Sending update notification to: " + subscriber.getUrl());
                        sendUpdate((SchoolUpdateDto) payload, subscriber);
                        break;
                    case SubscriberDto.EVENT_ON_DELETE:
                        log.info("Sending delete notification to: " + subscriber.getUrl());
                        sendDelete((SchoolEntityDTO) payload, subscriber);
                        break;
                    default:
                        log.warn("Unknown event type: " + eventType);
                        break;
                }
            }
        }
    }
}
