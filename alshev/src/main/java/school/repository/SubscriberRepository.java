package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.entity.Notification;
import school.entity.Subscriber;
import school.enums.NotificationStatus;

import java.util.List;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    List<Subscriber> findByUrl(String url);

}
