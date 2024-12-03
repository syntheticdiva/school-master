package school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.entity.SchoolEntity;
import school.mapper.SchoolMapper;

@Service
public class ThreadService {
    private SchoolNotificationThread schoolNotificationThread = null;

    @Autowired
    private SchoolMapper schoolMapper;
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
