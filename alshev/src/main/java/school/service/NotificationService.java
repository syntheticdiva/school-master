package school.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.dto.EventDto;
import school.entity.Notification;
import school.entity.SchoolEvent;
import school.enums.NotificationStatus;
import school.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private static final int MAX_RETRY_ATTEMPTS = 3;

    private final RestTemplate restTemplate;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @Value("${listener.url}")
    private String listenerUrl;

    @Autowired
    public NotificationService(
            RestTemplate restTemplate,
            NotificationRepository notificationRepository,
            ObjectMapper objectMapper
    ) {
        this.restTemplate = restTemplate;
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
    }

    public void sendNotification(SchoolEvent schoolEvent) {
        EventDto eventDto = createEventDto(schoolEvent);

        Notification notification = createNotification(eventDto, schoolEvent);

        try {
            sendToListener(eventDto);
            notification.setStatus(NotificationStatus.DELIVERED);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
        }

        notification.setLastAttemptTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }


    private void sendToListener(EventDto eventDto) {
        String notificationUrl = listenerUrl + "/event";

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    notificationUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(eventDto),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RestClientException("Non-successful HTTP status");
            }
        } catch (RestClientException e) {
            throw e;
        }
    }

    private Notification createNotification(EventDto eventDto, SchoolEvent schoolEvent) {
        Notification notification = new Notification();
        notification.setEventId(String.valueOf(schoolEvent.getSchoolId()));
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRetryCount(0);

        try {
            notification.setEventPayload(objectMapper.writeValueAsString(eventDto));
        } catch (JsonProcessingException e) {
            notification.setEventPayload("{}");
        }

        notification.setStatus(NotificationStatus.FAILED);

        return notification;
    }

    @Scheduled(fixedDelay = 1 * 60 * 1000)
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findByStatus(NotificationStatus.FAILED);

        failedNotifications.forEach(notification -> {
            if (notification.getRetryCount() < MAX_RETRY_ATTEMPTS) {
                processFailedNotification(notification);
            }
        });
    }

    private void processFailedNotification(Notification notification) {
        try {
            EventDto eventDto = parseEventPayload(notification.getEventPayload());

            sendToListener(eventDto);

            notification.setStatus(NotificationStatus.DELIVERED);
            notification.setLastAttemptTime(LocalDateTime.now());
        } catch (Exception e) {
            notification.setRetryCount(notification.getRetryCount() + 1);

            if (notification.getRetryCount() >= MAX_RETRY_ATTEMPTS) {
                notification.setStatus(NotificationStatus.FAILED);
            }
        } finally {
            notificationRepository.save(notification);
        }
    }

    private EventDto createEventDto(SchoolEvent schoolEvent) {
        EventDto eventDto = new EventDto();
        eventDto.setEntity("School");
        eventDto.setEventType(schoolEvent.getEventType());

        EventDto.MessageDto message = new EventDto.MessageDto();
        message.setTitle(generateTitle(schoolEvent));
        message.setContent(generateContent(schoolEvent));

        eventDto.setMsg(message);
        return eventDto;
    }

    private String generateTitle(SchoolEvent event) {
        return switch (event.getEventType()) {
            case "CREATE" -> "Создана школа: " + event.getName();
            case "UPDATE" -> "Обновлена школа: " + event.getName();
            case "DELETE" -> "Удалена школа: " + event.getName();
            default -> "Событие школы";
        };
    }

    private String generateContent(SchoolEvent event) {
        return switch (event.getEventType()) {
            case "CREATE" -> String.format(
                    "Создана школа: %s, адрес: %s, дата: %s",
                    event.getName(), event.getAddress(), event.getDate()
            );
            case "UPDATE" -> String.format(
                    "Обновлена школа: %s, старое имя: %s, новый адрес: %s, дата: %s",
                    event.getName(), event.getOldName(), event.getAddress(), event.getDate()
            );
            case "DELETE" -> String.format(
                    "Удалена школа: %s, адрес: %s, дата: %s",
                    event.getName(), event.getAddress(), event.getDate()
            );
            default -> "Неизвестное событие";
        };
    }

    private EventDto parseEventPayload(String payload) {
        try {
            return objectMapper.readValue(payload, EventDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid event payload", e);
        }
    }

}