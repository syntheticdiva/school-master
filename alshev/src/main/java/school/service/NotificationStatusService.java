package school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.dto.SubscriberDto;
import school.entity.NotificationStatus;
import school.enums.NotificationType;
import school.repository.NotificationStatusRepository;

@Service
@Slf4j
public class NotificationStatusService {
    private final NotificationStatusRepository notificationStatusRepository;

    @Autowired
    public NotificationStatusService(NotificationStatusRepository notificationStatusRepository) {
        this.notificationStatusRepository = notificationStatusRepository;
    }

    public void saveNotificationStatus(
            SubscriberDto subscriberDto,
            NotificationType type,
            String status,
            int attempts
    ) {
        try {
            if (subscriberDto == null) {
                log.error("Не удалось сохранить статус уведомления: subscriberDto равен null");
                return;
            }

            NotificationStatus notificationStatus = new NotificationStatus();
            notificationStatus.setSubscriberId(subscriberDto.getId());
            notificationStatus.setNotificationType(type.name());
            notificationStatus.setStatus(status);
            notificationStatus.setAttempts(attempts);

            notificationStatusRepository.save(notificationStatus);
            log.info("Статус уведомления успешно сохранен: {}", notificationStatus);
        } catch (Exception e) {
            log.error("Ошибка при сохранении статуса уведомления: {}", e.getMessage(), e);
        }
    }

    public void saveNotificationStatus(
            NotificationTask task,
            String status,
            int attempts
    ) {
        try {
            if (task.getSubscriberDto() == null) {
                log.error("Не удалось сохранить статус уведомления: subscriberDto равен null");
                return;
            }

            NotificationStatus notificationStatus = new NotificationStatus();
            notificationStatus.setSubscriberId(task.getSubscriberDto().getId());
            notificationStatus.setNotificationType(task.getType().name());
            notificationStatus.setStatus(status);
            notificationStatus.setAttempts(attempts);

            notificationStatusRepository.save(notificationStatus);
            log.info("Статус уведомления успешно сохранен: {}", notificationStatus);
        } catch (Exception e) {
            log.error("Ошибка при сохранении статуса уведомления: {}", e.getMessage(), e);
        }
    }
}