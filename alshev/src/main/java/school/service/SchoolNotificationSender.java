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

@Slf4j
@Service
public class SchoolNotificationSender {

    private final RestTemplate restTemplate;

    @Autowired
    public SchoolNotificationSender(
            RestTemplate restTemplate,
            SubscriberRepository subscriberRepository,
            SubscriberMapper subscriberMapper,
            ThreadService threadService
    ) {
        this.restTemplate = restTemplate;
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
}
