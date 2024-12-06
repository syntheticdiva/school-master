package school.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class SchoolNotificationThread extends Thread {
    private HashMap<String, ArrayList<SubscriberDto>> mapSubscribers;
    private ArrayList<SchoolEntityDTO> createdSchools = new ArrayList<>();
    private ArrayList<SchoolUpdateDto> updatedSchools = new ArrayList<>();
    private final ArrayList<SchoolEntityDTO> deletedSchools = new ArrayList<>();
    @Setter
    private final SchoolNotificationSender notificationSender;

    @Autowired
    public SchoolNotificationThread(SchoolNotificationSender notificationSender) {
        this.notificationSender = notificationSender;
        this.mapSubscribers = new HashMap<>();
        this.mapSubscribers.put(SubscriberDto.EVENT_ON_CREATE, new ArrayList<>());
        this.mapSubscribers.put(SubscriberDto.EVENT_ON_UPDATE, new ArrayList<>());
        this.mapSubscribers.put(SubscriberDto.EVENT_ON_DELETE, new ArrayList<>());
    }
    public void addSubscriber(SubscriberDto subscriberDto) {
        mapSubscribers.get(subscriberDto.getEventType()).add(subscriberDto);
        log.info("Added subscriber: " + subscriberDto);
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
            log.info("Created school " + first.toString() + " but no subscribers");
            return true;
        }

        for (SubscriberDto subscriber : subscribers) {
            this.notificationSender.sendCreate(first, subscriber);
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

        for (int i = 0; i < subscribers.size(); i++) {
            SubscriberDto subscriber = subscribers.get(i);
            this.notificationSender.sendUpdate(first, subscriber);
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

        for (int i = 0; i < subscribers.size(); i++) {
            SubscriberDto subscriber = subscribers.get(i);
            this.notificationSender.sendDelete(first, subscriber);
        }
        return true;
    }
}
