package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.entity.Subscriber;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
}
