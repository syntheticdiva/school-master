package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.entity.Notification;
import school.enums.NotificationStatus;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStatus(NotificationStatus status);
}
