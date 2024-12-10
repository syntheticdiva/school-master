package school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.dto.SubscriberDto;
import school.entity.NotificationStatus;
import school.enums.NotificationType;
import school.exception.NotificationStatusPersistenceException;
import school.exception.SubscriberNotFoundException;
import school.repository.NotificationStatusRepository;

@Service
public class NotificationStatusService {
    private final NotificationStatusRepository notificationStatusRepository;

    @Autowired
    public NotificationStatusService(NotificationStatusRepository notificationStatusRepository) {
        this.notificationStatusRepository = notificationStatusRepository;
    }

    @Transactional
    public void saveNotificationStatus(
            SubscriberDto subscriberDto,
            NotificationType type,
            String status,
            int attempts
    ) {
        if (subscriberDto == null) {
            throw new SubscriberNotFoundException("The notification status could not be saved: subscriberDto is null");
        }

        NotificationStatus notificationStatus = new NotificationStatus();
        notificationStatus.setSubscriberId(subscriberDto.getId());
        notificationStatus.setNotificationType(type.name());
        notificationStatus.setStatus(status);
        notificationStatus.setAttempts(attempts);

        try {
            notificationStatusRepository.save(notificationStatus);
        } catch (DataAccessException e) {
            throw new NotificationStatusPersistenceException("An error occurred while saving the notification status.", e);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Transactional
    public void saveNotificationStatus(
            NotificationTask task,
            String status,
            int attempts
    ) {
        if (task.getSubscriberDto() == null) {
            throw new SubscriberNotFoundException("The notification status could not be saved: subscriberDto is null");
        }

        NotificationStatus notificationStatus = new NotificationStatus();
        notificationStatus.setSubscriberId(task.getSubscriberDto().getId());
        notificationStatus.setNotificationType(task.getType().name());
        notificationStatus.setStatus(status);
        notificationStatus.setAttempts(attempts);

        try {
            notificationStatusRepository.save(notificationStatus);
        } catch (DataAccessException e) {
            throw new NotificationStatusPersistenceException("An error occurred while saving the notification status.", e);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}