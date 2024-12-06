package school.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.dto.SubscriberDto;

import school.entity.SubscriberEntity;
import school.repository.SubscriberRepository;
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

    @Autowired
    public SubscriberController(SubscriberService subscriberService,
                                ThreadService threadService) {
        this.subscriberService = subscriberService;
        this.threadService = threadService;
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
}
