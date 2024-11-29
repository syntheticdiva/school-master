package school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import school.entity.Subscriber;
import school.service.SubscriberService;

import java.util.List;

@RestController
@RequestMapping("/subscribers")
public class SubscriberController {
    private final SubscriberService subscriberService;

    @Autowired
    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping
    public void addSubscriber(@RequestBody String url) {
        subscriberService.addSubscriber(url);
    }

    @GetMapping
    public List<Subscriber> getSubscribers() {
        return subscriberService.getAllSubscribers();
    }

    @DeleteMapping("/{id}")
    public void removeSubscriber(@PathVariable Long id) {
        subscriberService.removeSubscriber(id);
    }
}
