package school.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.service.SchoolNotificationSender;
import school.service.SchoolService;
import school.service.ThreadService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(SchoolRestController.BASE_URL)
@Slf4j
public class SchoolRestController {
    public static final String BASE_URL = "/api/schools";
    private final SchoolService schoolService;
    private final SchoolNotificationSender notificationSender;
    private final ThreadService threadService;

    @Autowired
    public SchoolRestController(SchoolService schoolService,
                                SchoolNotificationSender notificationSender,
                                ThreadService threadService) {
        this.schoolService = schoolService;
        this.notificationSender = notificationSender;
        this.threadService = threadService;
    }

    @PostMapping
    public ResponseEntity<SchoolEntityDTO> createSchool(@Valid @RequestBody SchoolCreateDTO schoolCreateDTO) {
        SchoolEntityDTO createdSchool = schoolService.create(schoolCreateDTO);
        notifySubscribers(SubscriberDto.EVENT_ON_CREATE, createdSchool);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchool);
    }
    @PutMapping("/{id}")
    public ResponseEntity<SchoolEntityDTO> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolEntityDTO schoolEntityDTO) {
        SchoolEntityDTO oldSchool = schoolService.findById(id);

        SchoolEntityDTO updatedSchool = schoolService.update(id, schoolEntityDTO);

        notifySubscribers(SubscriberDto.EVENT_ON_UPDATE, new SchoolUpdateDto(oldSchool, updatedSchool));

        return ResponseEntity.ok(updatedSchool);
    }

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteSchool(@PathVariable Long id) {
    try {
        SchoolEntityDTO deletedSchool = schoolService.findById(id);

        log.info("Attempting to delete school with ID: {}", id);

        List<SubscriberDto> subscribers = threadService.getSubscribers().stream()
                .filter(s -> s.getEntity().equals(SubscriberDto.ENTITY_SCHOOL) &&
                        s.getEventType().equals(SubscriberDto.EVENT_ON_DELETE))
                .collect(Collectors.toList());

        log.info("Found {} subscribers for delete event", subscribers.size());

        schoolService.delete(id);

        return ResponseEntity.noContent().build();
    } catch (Exception e) {
        log.error("Error deleting school with ID {}: {}", id, e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    private void notifySubscribers(String eventType, Object payload) {
        List<SubscriberDto> subscribers = threadService.getSubscribers();
        log.info("Notifying subscribers for event type: " + eventType + " with payload: " + payload);

        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(eventType) &&
                    subscriber.getEntity().equals(SubscriberDto.ENTITY_SCHOOL)) {

                switch (eventType) {
                    case SubscriberDto.EVENT_ON_CREATE:
                        log.info("Sending create notification to: " + subscriber.getUrl());
                        notificationSender.sendCreate((SchoolEntityDTO) payload, subscriber);
                        break;
                    case SubscriberDto.EVENT_ON_UPDATE:
                        log.info("Sending update notification to: " + subscriber.getUrl());
                        notificationSender.sendUpdate((SchoolUpdateDto) payload, subscriber);
                        break;
                    case SubscriberDto.EVENT_ON_DELETE:
                        log.info("Sending delete notification to: " + subscriber.getUrl());
                        notificationSender.sendDelete((SchoolEntityDTO) payload, subscriber);
                        break;
                    default:
                        log.warn("Unknown event type: " + eventType);
                        break;
                }
            }
        }
    }
}




