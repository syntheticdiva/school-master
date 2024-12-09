package school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.dto.*;
import school.entity.NotificationStatus;
import school.enums.NotificationType;
import school.mapper.SubscriberMapper;
import school.repository.NotificationStatusRepository;
import school.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
public class SchoolNotificationSender {
    private final RestTemplate restTemplate;
    private final NotificationStatusRepository notificationStatusRepository;

    @Autowired
    public SchoolNotificationSender(
            RestTemplate restTemplate,
            NotificationStatusRepository notificationStatusRepository
    ) {
        this.restTemplate = restTemplate;
        this.notificationStatusRepository = notificationStatusRepository;
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

            saveNotificationStatus(subscriberDto, NotificationType.CREATE, "доставлено", 1);
        } catch (RestClientException e) {
            log.error("Failed to send creation notification to {}", subscriberDto.getUrl(), e);

            saveNotificationStatus(subscriberDto, NotificationType.CREATE, "не доставлено", 1);
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

            saveNotificationStatus(subscriberDto, NotificationType.UPDATE, "доставлено", 1);
        } catch (RestClientException e) {
            log.error("Failed to send update notification to {}", subscriberDto.getUrl(), e);

            saveNotificationStatus(subscriberDto, NotificationType.UPDATE, "не доставлено", 1);
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

            saveNotificationStatus(subscriberDto, NotificationType.DELETE, "доставлено", 1);
        } catch (RestClientException e) {
            log.error("Failed to send deletion notification to {}", subscriberDto.getUrl(), e);

            saveNotificationStatus(subscriberDto, NotificationType.DELETE, "не доставлено", 1);
        }
    }

    private void saveNotificationStatus(SubscriberDto subscriberDto, NotificationType type, String status, int attempts) {
        try {
            if (subscriberDto == null) {
                log.error("Не удалось сохранить статус уведомления: subscriberDto равен null");
                return;
            }

            NotificationStatus notificationStatus = new NotificationStatus();
            notificationStatus.setSubscriberId(subscriberDto.getId());
            notificationStatus.setNotificationType(type.name());
            notificationStatus.setStatus(status);
            notificationStatus.setAttempts(attempts);

            notificationStatusRepository.save(notificationStatus);
            log.info("Статус уведомления успешно сохранен: {}", notificationStatus);
        } catch (Exception e) {
            log.error("Ошибка при сохранении статуса уведомления: {}", e.getMessage(), e);
        }
    }
}
