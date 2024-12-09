package school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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

    @Autowired
    public SchoolNotificationSender(
            RestTemplate restTemplate,
            NotificationStatusService notificationStatusService
    ) {
        this.restTemplate = restTemplate;
        this.notificationStatusService = notificationStatusService;
    }

    public void sendCreate(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        sendCreateWithRetry(schoolEntityDTO, subscriberDto);
    }

    public void sendUpdate(SchoolUpdateDto schoolUpdateDto, SubscriberDto subscriberDto) {
        sendUpdateWithRetry(schoolUpdateDto, subscriberDto);
    }

    public void sendDelete(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        sendDeleteWithRetry(schoolEntityDTO, subscriberDto);
    }

    public void sendCreateWithRetry(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        int maxRetries = 3;
        long retryInterval = 5000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
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
                log.error("Ошибка отправки уведомления. Попытка {}/{}", attempt, maxRetries, e);

                if (attempt == maxRetries) {
                    notificationStatusService.saveNotificationStatus(
                            subscriberDto,
                            NotificationType.CREATE,
                            "не доставлено",
                            attempt
                    );
                    break;
                }

                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException ie) {}
            }
        }
    }

    public void sendUpdateWithRetry(SchoolUpdateDto schoolUpdateDto, SubscriberDto subscriberDto) {
        int maxRetries = 3;
        long retryInterval = 5000;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
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
                log.error("Ошибка отправки уведомления. Попытка {}/{}", attempt, maxRetries, e);

                if (attempt == maxRetries) {
                    notificationStatusService.saveNotificationStatus(
                            subscriberDto,
                            NotificationType.UPDATE,
                            "не доставлено",
                            attempt
                    );
                    break;
                }

                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException ie) {}
            }
        }
    }

    public void sendDeleteWithRetry(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        int maxRetries = 3;
        long retryInterval = 5000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
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
                log.error("Ошибка отправки уведомления. Попытка {}/{}", attempt, maxRetries, e);

                if (attempt == maxRetries) {
                    notificationStatusService.saveNotificationStatus(
                            subscriberDto,
                            NotificationType.DELETE,
                            "не доставлено",
                            attempt
                    );
                    break;
                }

                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException ie) {}
            }
        }
    }
}