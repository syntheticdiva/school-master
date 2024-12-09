package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.entity.NotificationStatus;

public interface NotificationStatusRepository extends JpaRepository<NotificationStatus, Long> {
}