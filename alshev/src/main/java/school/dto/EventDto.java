package school.dto;

import lombok.Data;

@Data
public class EventDto {
    private String entity;
    private String eventType;
    private MessageDto msg;

    @Data
    public static class MessageDto {
        private String title;
        private String content;
    }
}