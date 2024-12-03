package school.dto;

import lombok.Data;

@Data
public class SubscriberDto {
    public static final String ENTITY_SCHOOL = "school";
    public static final String EVENT_ON_CREATE = "on_create";
    public static final String EVENT_ON_UPDATE = "on_update";
    public static final String EVENT_ON_DELETE = "on_delete";
    private String entity;
    private String eventType;
    private String url;
}
