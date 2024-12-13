package school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.dto.*;
import school.enums.NotificationType;

import java.time.LocalDateTime;

@Slf4j
@Service
public class SchoolNotificationSender {
    private final RestTemplate restTemplate;
    private final NotificationStatusService notificationStatusService;

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_INTERVAL = 5000;

    @Autowired
    public SchoolNotificationSender(
            RestTemplate restTemplate,
            NotificationStatusService notificationStatusService
    ) {
        this.restTemplate = restTemplate;
        this.notificationStatusService = notificationStatusService;
    }

    @Async
    public void sendCreate(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        boolean delivered = false;
        for (int attempt = 1; attempt <= MAX_RETRIES && !delivered; attempt++) {
            try {
                SchoolOnCreateDto createDto = new SchoolOnCreateDto(
                        schoolEntityDTO.getId(),
                        schoolEntityDTO.getName(),
                        schoolEntityDTO.getAddress(),
                        LocalDateTime.now()
                );

                ResponseEntity<String> response = restTemplate.postForEntity(
                        subscriberDto.getUrl(),
                        createDto,
                        String.class
                );

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new RestClientException("Unsuccessful response: " + response.getStatusCode());
                }

                notificationStatusService.saveNotificationStatus(
                        subscriberDto,
                        NotificationType.CREATE,
                        "доставлено",
                        attempt
                );
                delivered = true;
                return;
            } catch (RestClientException e) {
                if (attempt == MAX_RETRIES) {
                    notificationStatusService.saveNotificationStatus(
                            subscriberDto,
                            NotificationType.CREATE,
                            "не доставлено",
                            attempt
                    );
                    break;
                }
                sleep();
            }
        }
    }


    @Async
    public void sendUpdate(SchoolUpdateDto schoolUpdateDto, SubscriberDto subscriberDto) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(
                        subscriberDto.getUrl(),
                        schoolUpdateDto,
                        String.class
                );

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new RestClientException("Unsuccessful response: " + response.getStatusCode());
                }

                notificationStatusService.saveNotificationStatus(
                        subscriberDto,
                        NotificationType.UPDATE,
                        "доставлено",
                        attempt
                );
                return;
            } catch (RestClientException e) {
                if (attempt == MAX_RETRIES) {
                    notificationStatusService.saveNotificationStatus(
                            subscriberDto,
                            NotificationType.UPDATE,
                            "не доставлено",
                            attempt
                    );
                    break;
                }
                sleep();
            }
        }
    }

    @Async
    public void sendDelete(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                SchoolOnDeleteDto deleteDto = new SchoolOnDeleteDto(
                        schoolEntityDTO.getId(),
                        schoolEntityDTO.getName(),
                        schoolEntityDTO.getAddress()
                );

                ResponseEntity<String> response = restTemplate.postForEntity(
                        subscriberDto.getUrl(),
                        deleteDto,
                        String.class
                );

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new RestClientException("Unsuccessful response: " + response.getStatusCode());
                }

                notificationStatusService.saveNotificationStatus(
                        subscriberDto,
                        NotificationType.DELETE,
                        "доставлено",
                        attempt
                );
                return;
            } catch (RestClientException e) {
                if (attempt == MAX_RETRIES) {
                    notificationStatusService.saveNotificationStatus(
                            subscriberDto,
                            NotificationType.DELETE,
                            "не доставлено",
                            attempt
                    );
                    break;
                }
                sleep();
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(RETRY_INTERVAL);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
