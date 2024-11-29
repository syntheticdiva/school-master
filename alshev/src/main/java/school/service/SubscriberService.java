package school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.entity.Subscriber;
import school.repository.SubscriberRepository;

import java.util.List;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;

    @Autowired
    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public void addSubscriber(String url) {
        Subscriber subscriber = new Subscriber();
        subscriber.setUrl(url);
        subscriberRepository.save(subscriber);
    }

    public List<Subscriber> getAllSubscribers() {
        return subscriberRepository.findAll();
    }

    public void removeSubscriber(Long id) {
        subscriberRepository.deleteById(id);
    }
}