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
    private final NotificationStatusService notificationStatusService;
    private final RestTemplate restTemplate;

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_INTERVAL = 5000;

    @Autowired
    public SchoolNotificationSender(NotificationStatusService notificationStatusService, RestTemplate restTemplate) {
        this.notificationStatusService = notificationStatusService;
        this.restTemplate = restTemplate;
    }
    @Async
    public void sendCreate(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
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

                log.info("Creation notification sent successfully to {}. Response: {}",
                        subscriberDto.getUrl(), response);

                notificationStatusService.saveNotificationStatus(
                        subscriberDto,
                        NotificationType.CREATE,
                        "доставлено",
                        attempt
                );
                return;
            } catch (RestClientException e) {
                log.error("Ошибка отправки уведомления. Попытка {}/{}", attempt, MAX_RETRIES, e);

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

                log.info("Update notification sent successfully to {}. Response: {}",
                        subscriberDto.getUrl(), response);

                notificationStatusService.saveNotificationStatus(
                        subscriberDto,
                        NotificationType.UPDATE,
                        "доставлено",
                        attempt
                );
                return;
            } catch (RestClientException e) {
                log.error("Ошибка отправки уведомления. Попытка {}/{}", attempt, MAX_RETRIES, e);

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

                log.info("Deletion notification sent successfully to {}. Response: {}",
                        subscriberDto.getUrl(), response);

                notificationStatusService.saveNotificationStatus(
                        subscriberDto,
                        NotificationType.DELETE,
                        "доставлено",
                        attempt
                );
                return;
            } catch (RestClientException e) {
                log.error("Ошибка отправки уведомления. Попытка {}/{}", attempt, MAX_RETRIES, e);

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
        } catch (InterruptedException ie) {}
    }
}

