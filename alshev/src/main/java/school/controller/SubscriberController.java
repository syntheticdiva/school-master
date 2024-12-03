package school.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import school.dto.SubscriberDto;
import school.service.ThreadService;

@RestController
@RequestMapping("/subscribers")
public class SubscriberController {
    @Autowired
    private ThreadService threadService;

    @PostMapping
    @Operation(summary = "create subscriber",
            description = "create subscriber")
    public void addSubscriber(@RequestBody SubscriberDto subscriberDto) {
        threadService.addSubscriber(subscriberDto);
    }
}
