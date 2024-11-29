package school.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SchoolEvent {
    private Long schoolId;
    private String name;
    private String address;
    private LocalDateTime date;
    private String eventType;
    private String oldName;
    private String oldAddress;
}
