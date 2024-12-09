package school.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.entity.NotificationStatus;
import school.repository.NotificationStatusRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class SchoolNotificationThread extends Thread {
    private HashMap<String, ArrayList<SubscriberDto>> mapSubscribers;
    private ArrayList<SchoolEntityDTO> createdSchools = new ArrayList<>();
    private ArrayList<SchoolUpdateDto> updatedSchools = new ArrayList<>();
    private final ArrayList<SchoolEntityDTO> deletedSchools = new ArrayList<>();
    @Setter
    private final SchoolNotificationSender notificationSender;
    private final NotificationStatusService notificationStatusService;
    private final int maxRetries = 3;
    private final long retryInterval = 5000;

    @Autowired
    public SchoolNotificationThread(
            SchoolNotificationSender notificationSender, NotificationStatusService notificationStatusService
    ) {
        this.notificationSender = notificationSender;
        this.notificationStatusService = notificationStatusService;

        this.mapSubscribers = new HashMap<>();
        this.mapSubscribers.put(SubscriberDto.EVENT_ON_CREATE, new ArrayList<>());
        this.mapSubscribers.put(SubscriberDto.EVENT_ON_UPDATE, new ArrayList<>());
        this.mapSubscribers.put(SubscriberDto.EVENT_ON_DELETE, new ArrayList<>());
    }
    public void addSubscriber(SubscriberDto subscriberDto) {
        mapSubscribers.get(subscriberDto.getEventType()).add(subscriberDto);
        log.info("Added subscriber: " + subscriberDto);
    }
    public void removeSubscriber(Long subscriberId) {
        for (String eventType : mapSubscribers.keySet()) {
            ArrayList<SubscriberDto> subscribers = mapSubscribers.get(eventType);
            for (int i = 0; i < subscribers.size(); i++) {
                if (subscribers.get(i).getId().equals(subscriberId)) {
                    subscribers.remove(i);
                    log.info("Removed subscriber with ID: " + subscriberId + " for event type: " + eventType);
                    break;
                }
            }
        }
    }
    public List<SubscriberDto> getSubscribers() {
        ArrayList<SubscriberDto> allSubscribers = new ArrayList<>();
        for (ArrayList<SubscriberDto> subscribers : mapSubscribers.values()) {
            allSubscribers.addAll(subscribers);
        }
        return allSubscribers;
    }

    public void addSchoolCreated(SchoolEntityDTO schoolEntityDTO) {
        log.info("Adding created school to thread: " + schoolEntityDTO);
        createdSchools.add(schoolEntityDTO);
    }
    public void addSchoolUpdated (SchoolUpdateDto schoolUpdateDto){

        updatedSchools.add(schoolUpdateDto);
    }
    public void addSchoolDeleted (SchoolEntityDTO schoolEntityDTO){

        deletedSchools.add(schoolEntityDTO);
    }
    @Override
    public void run() {
        while (true){
            boolean sent = false;
            sent = sent || checkAndSendCreate();
            sent = sent || checkAndSendUpdate();
            sent = sent || checkAndSendDelete();
            if (!sent) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e){}
            }
        }
    }
    private boolean checkAndSendCreate() {
        if (createdSchools.isEmpty())
            return false;

        SchoolEntityDTO first = createdSchools.remove(0);
        ArrayList<SubscriberDto> subscribers = mapSubscribers.get(SubscriberDto.EVENT_ON_CREATE);

        if (subscribers.isEmpty()) {
            log.info("Created school " + first.toString() + " but no subscribers. Current subscribers: " + getSubscribers());
            return true;
        }

        for (SubscriberDto subscriber : subscribers) {
            NotificationTask task = new NotificationTask(first, subscriber);
            processNotification(task);
        }
        return true;
    }
    private boolean checkAndSendUpdate() {
        if (updatedSchools.isEmpty())
            return false;

        SchoolUpdateDto first = updatedSchools.get(0);
        updatedSchools.remove(0);
        ArrayList<SubscriberDto> subscribers = mapSubscribers.get(SubscriberDto.EVENT_ON_UPDATE);

        if (subscribers.isEmpty()) {
            log.info("The message " + first.toString() + " but no subscribers");
            return true;
        }

        for (SubscriberDto subscriber : subscribers) {
            NotificationTask task = new NotificationTask(first, subscriber);
            processNotification(task);
        }
        return true;
    }

private boolean checkAndSendDelete() {
    if (deletedSchools.isEmpty())
        return false;

    SchoolEntityDTO first = deletedSchools.remove(0);
    ArrayList<SubscriberDto> subscribers = mapSubscribers.get(SubscriberDto.EVENT_ON_DELETE);

    if (subscribers.isEmpty()) {
        log.info("Deleted school " + first.toString() + " but no subscribers");
        return true;
    }

    for (SubscriberDto subscriber : subscribers) {
        NotificationTask task = new NotificationTask(first, subscriber, true);
        processNotification(task);
    }
    return true;
}
    public void sendNotification(NotificationTask task) throws Exception {
        switch (task.getType()) {
            case CREATE:
                notificationSender.sendCreate(task.getSchoolEntityDTO(), task.getSubscriberDto());
                break;
            case UPDATE:
                notificationSender.sendUpdate(task.getSchoolUpdateDto(), task.getSubscriberDto());
                break;
            case DELETE:
                notificationSender.sendDelete(task.getSchoolEntityDTO(), task.getSubscriberDto());
                break;
            default:
                throw new IllegalArgumentException("Unsupported notification type: " + task.getType());
        }
    }


    private void processNotification(NotificationTask task) {
        int attempt = 0;
        boolean delivered = false;

        while (attempt < maxRetries && !delivered) {
            try {
                sendNotification(task);
                delivered = true;
                log.info("Уведомление успешно доставлено: {}", task);

                notificationStatusService.saveNotificationStatus(task, "доставлено", attempt + 1);
            } catch (Exception e) {
                attempt++;
                log.error("Ошибка при отправке уведомления: {}. Попытка {}/{}", task, attempt, maxRetries);

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                } else {
                    log.error("Не удалось доставить уведомление после {} попыток: {}", maxRetries, task);
                    notificationStatusService.saveNotificationStatus(task, "не доставлено", attempt);
                }
            }
        }
    }
}
