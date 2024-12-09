package school.service;

import lombok.Data;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.enums.NotificationType;

@Data
public class NotificationTask {
    private SchoolEntityDTO schoolEntityDTO;
    private SchoolUpdateDto schoolUpdateDto;
    private SubscriberDto subscriberDto;
    private NotificationType type;
    private String status;

    public NotificationTask(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        this.schoolEntityDTO = schoolEntityDTO;
        this.subscriberDto = subscriberDto;
        this.type = NotificationType.CREATE;
    }

    public NotificationTask(SchoolUpdateDto schoolUpdateDto, SubscriberDto subscriberDto) {
        this.schoolUpdateDto = schoolUpdateDto;
        this.subscriberDto = subscriberDto;
        this.type = NotificationType.UPDATE;
    }

    public NotificationTask(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto, boolean isDelete) {
        this.schoolEntityDTO = schoolEntityDTO;
        this.subscriberDto = subscriberDto;
        this.type = NotificationType.DELETE;
    }
}
