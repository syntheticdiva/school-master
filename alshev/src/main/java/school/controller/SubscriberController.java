package school.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.dto.SubscriberDto;
import school.entity.SubscriberEntity;
import school.service.SubscriberService;
import school.service.ThreadService;

import java.util.List;

@RestController
@RequestMapping("/subscribers")
public class SubscriberController {
    private final SubscriberService subscriberService;
    private final ThreadService threadService;

    @Autowired
    public SubscriberController(SubscriberService subscriberService,
                                ThreadService threadService) {
        this.subscriberService = subscriberService;
        this.threadService = threadService;
    }

    @PostMapping
    public ResponseEntity<SubscriberEntity> addSubscriber(@Valid @RequestBody SubscriberDto subscriberDto) {
        SubscriberEntity savedEntity = subscriberService.createSubscriber(subscriberDto);
        threadService.addSubscriber(subscriberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEntity);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing subscriber")
    public ResponseEntity<SubscriberEntity> updateSubscriber(
            @PathVariable Long id,
            @Valid @RequestBody SubscriberDto subscriberDto
    ) {
        SubscriberEntity updatedEntity = subscriberService.updateSubscriber(id, subscriberDto);
        return ResponseEntity.ok(updatedEntity);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subscriber by ID")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable Long id) {
        subscriberService.deleteSubscriber(id);
        return ResponseEntity.noContent().build();
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