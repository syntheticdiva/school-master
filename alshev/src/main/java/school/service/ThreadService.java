package school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.mapper.SchoolMapper;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.dto.SubscriberDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ThreadService {
    private SchoolNotificationThread schoolNotificationThread = null;

    @Autowired
    private SchoolNotificationSender notificationSender;

    private void checkAndStart() {
        if (schoolNotificationThread != null)
            return;
        schoolNotificationThread = new SchoolNotificationThread(notificationSender);
        schoolNotificationThread.start();
    }

    public void addSubscriber(SubscriberDto subscriberDto) {
        checkAndStart();
        schoolNotificationThread.addSubscriber(subscriberDto);
    }

    public List<SubscriberDto> getSubscribers() {
        return schoolNotificationThread.getSubscribers();
    }

    public void addSchoolCreated(SchoolEntityDTO schoolEntityDTO) {
        checkAndStart();
        schoolNotificationThread.addSchoolCreated(schoolEntityDTO);
    }

    public void addSchoolUpdated(SchoolUpdateDto schoolUpdateDto) {
        checkAndStart();
        schoolNotificationThread.addSchoolUpdated(schoolUpdateDto);
    }

    public void addSchoolDeleted(SchoolEntityDTO schoolEntityDTO) {
        checkAndStart();
        schoolNotificationThread.addSchoolDeleted(schoolEntityDTO);
    }
}