package school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.dto.EventDto;
import school.entity.SchoolEvent;
import school.entity.Subscriber;
import school.repository.SubscriberRepository;

import java.util.List;

@Service
@Slf4j
public class NotificationService {
    private final SubscriberRepository subscriberRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public NotificationService(
            SubscriberRepository subscriberRepository,
            RestTemplate restTemplate
    ) {
        this.subscriberRepository = subscriberRepository;
        this.restTemplate = restTemplate;
    }

    public void sendNotification(SchoolEvent schoolEvent) {
        EventDto eventDto = createEventDto(schoolEvent);
        List<Subscriber> subscribers = subscriberRepository.findAll();

        subscribers.forEach(subscriber -> {
            String notificationUrl = subscriber.getUrl() + "/event";

            restTemplate.exchange(
                    notificationUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(eventDto),
                    String.class
            );
        });
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
        switch (event.getEventType()) {
            case "CREATE":
                return "Создана школа: " + event.getName();
            case "UPDATE":
                return "Обновлена школа: " + event.getName();
            case "DELETE":
                return "Удалена школа: " + event.getName();
            default:
                return "Событие школы";
        }
    }

    private String generateContent(SchoolEvent event) {
        switch (event.getEventType()) {
            case "CREATE":
                return String.format(
                        "Создана школа: %s, адрес: %s, дата: %s",
                        event.getName(),
                        event.getAddress(),
                        event.getDate()
                );
            case "UPDATE":
                return String.format(
                        "Обновлена школа: %s, старое имя: %s, новый адрес: %s, дата: %s",
                        event.getName(),
                        event.getOldName(),
                        event.getAddress(),
                        event.getDate()
                );
            case "DELETE":
                return String.format(
                        "Удалена школа: %s, адрес: %s, дата: %s",
                        event.getName(),
                        event.getAddress(),
                        event.getDate()
                );
            default:
                return "Неизвестное событие";
        }
    }
}