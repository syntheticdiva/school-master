package school.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import school.dto.NotificationTask;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.exception.NotificationProcessingException;
import school.exception.SubscriberNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SchoolNotificationThread extends Thread {
    private HashMap<String, ArrayList<SubscriberDto>> mapSubscribers;
    private ArrayList<SchoolEntityDTO> createdSchools = new ArrayList<>();
    private ArrayList<SchoolUpdateDto> updatedSchools = new ArrayList<>();
    private final ArrayList<SchoolEntityDTO> deletedSchools = new ArrayList<>();

    @Setter
    private final SchoolNotificationSender notificationSender;
    private final NotificationStatusService notificationStatusService;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_INTERVAL = 5000;

    @Autowired
    public SchoolNotificationThread(
            SchoolNotificationSender notificationSender,
            NotificationStatusService notificationStatusService
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
    }

    public void removeSubscriber(Long subscriberId) {
        boolean found = false;
        for (String eventType : mapSubscribers.keySet()) {
            ArrayList<SubscriberDto> subscribers = mapSubscribers.get(eventType);
            for (int i = 0; i < subscribers.size(); i++) {
                if (subscribers.get(i).getId().equals(subscriberId)) {
                    subscribers.remove(i);
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            throw new SubscriberNotFoundException("Subscriber with ID " + subscriberId + " not found.");
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
        createdSchools.add(schoolEntityDTO);
    }

    public void addSchoolUpdated(SchoolUpdateDto schoolUpdateDto) {
        updatedSchools.add(schoolUpdateDto);
    }

    public void addSchoolDeleted(SchoolEntityDTO schoolEntityDTO) {
        deletedSchools.add(schoolEntityDTO);
    }

    @Override
    public void run() {
        while (true) {
            boolean sent = false;
            sent = sent || checkAndSendCreate();
            sent = sent || checkAndSendUpdate();
            sent = sent || checkAndSendDelete();
            if (!sent) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
        }
    }

    private boolean checkAndSendCreate() {
        if (createdSchools.isEmpty()) {
            return false;
        }

        SchoolEntityDTO first = createdSchools.remove(0);
        ArrayList<SubscriberDto> subscribers = mapSubscribers.get(SubscriberDto.EVENT_ON_CREATE);

        if (subscribers.isEmpty()) {
            return true;
        }

        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(SubscriberDto.EVENT_ON_CREATE)) {
                NotificationTask task = new NotificationTask(first, subscriber);
                processNotification(task);
            }
        }
        return true;
    }

    private boolean checkAndSendUpdate() {
        if (updatedSchools.isEmpty()) {
            return false;
        }

        SchoolUpdateDto first = updatedSchools.remove(0);
        ArrayList<SubscriberDto> subscribers = mapSubscribers.get(SubscriberDto.EVENT_ON_UPDATE);

        if (subscribers.isEmpty()) {
            return true;
        }

        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(SubscriberDto.EVENT_ON_UPDATE)) {
                NotificationTask task = new NotificationTask(first, subscriber);
                processNotification(task);
            }
        }
        return true;
    }

    private boolean checkAndSendDelete() {
        if (deletedSchools.isEmpty()) {
            return false;
        }

        SchoolEntityDTO first = deletedSchools.remove(0);
        ArrayList<SubscriberDto> subscribers = mapSubscribers.get(SubscriberDto.EVENT_ON_DELETE);

        if (subscribers.isEmpty()) {
            return true;
        }

        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(SubscriberDto.EVENT_ON_DELETE)) {
                NotificationTask task = new NotificationTask(first, subscriber, true);
                processNotification(task);
            }
        }
        return true;
    }

    public void sendNotification(NotificationTask task) throws NotificationProcessingException {
        try {
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
        } catch (NotificationProcessingException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new NotificationProcessingException("Error processing notification: " + task, e);
        }
    }

    private void processNotification(NotificationTask task) {
        int attempt = 0;
        boolean delivered = false;

        while (attempt < MAX_RETRIES && !delivered) {
            attempt++;
            try {
                sendNotification(task);
                notificationStatusService.saveNotificationStatus(task, "доставлено", attempt);
                delivered = true;
            } catch (Exception e) {
                if (attempt == MAX_RETRIES) {
                    notificationStatusService.saveNotificationStatus(task, "не доставлено", attempt);
                } else {
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}