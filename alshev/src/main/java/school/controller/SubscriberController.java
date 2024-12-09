package school.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.dto.SubscriberDto;

import school.entity.SubscriberEntity;
import school.repository.SubscriberRepository;
import school.service.SchoolNotificationThread;
import school.service.SubscriberService;
import school.service.ThreadService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/subscribers")
@Slf4j
public class SubscriberController {
    private final SubscriberService subscriberService;
    private final ThreadService threadService;

    private final SchoolNotificationThread notificationThread;

    @Autowired
    public SubscriberController(SubscriberService subscriberService,
                                ThreadService threadService, SchoolNotificationThread notificationThread) {
        this.subscriberService = subscriberService;
        this.threadService = threadService;
        this.notificationThread = notificationThread;
    }

    @PostMapping
    public ResponseEntity<?> addSubscriber(@Valid @RequestBody SubscriberDto subscriberDto) {
        try {
            SubscriberEntity savedEntity = subscriberService.createSubscriber(subscriberDto);
            threadService.addSubscriber(subscriberDto);
            return ResponseEntity.ok(savedEntity);
        } catch (Exception e) {
            log.error("Error adding subscriber", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add subscriber");
        }
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update existing subscriber")
    public ResponseEntity<?> updateSubscriber(
            @PathVariable Long id,
            @Valid @RequestBody SubscriberDto subscriberDto
    ) {
        try {
            SubscriberEntity updatedEntity = subscriberService.updateSubscriber(id, subscriberDto);
            return ResponseEntity.ok(updatedEntity);
        } catch (EntityNotFoundException e) {
            log.error("Error updating subscriber", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Subscriber not found");
        } catch (Exception e) {
            log.error("Error updating subscriber", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update subscriber");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subscriber by ID")
    public ResponseEntity<?> deleteSubscriber(@PathVariable Long id) {
        try {
            subscriberService.deleteSubscriber(id);
            return ResponseEntity.ok("Subscriber deleted successfully");
        } catch (EntityNotFoundException e) {
            log.error("Error deleting subscriber", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Subscriber not found");
        } catch (Exception e) {
            log.error("Error deleting subscriber", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete subscriber");
        }
    }
    @GetMapping
    @Operation(summary = "Get all subscribers")
    public ResponseEntity<List<SubscriberDto>> getAllSubscribers() {
        List<SubscriberDto> subscribers = subscriberService.getAllSubscribers();

        if (subscribers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(subscribers);
    }
}
