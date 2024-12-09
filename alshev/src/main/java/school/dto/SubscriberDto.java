package school.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriberDto {
    public static final String ENTITY_SCHOOL = "school";
    public static final String EVENT_ON_CREATE = "on_create";
    public static final String EVENT_ON_UPDATE = "on_update";
    public static final String EVENT_ON_DELETE = "on_delete";
    private Long id;
    @NotNull(message = "Entity type cannot be null")
    private String entity;
    @NotNull(message = "Event type cannot be null")
    private String eventType;
    @NotBlank(message = "URL cannot be blank")
    private String url;

}
