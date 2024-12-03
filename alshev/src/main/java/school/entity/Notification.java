package school.entity;

import jakarta.persistence.*;
import lombok.Data;
import school.enums.NotificationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventId;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(nullable = false)
    private int retryCount;

    private LocalDateTime lastAttemptTime;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 1000)
    private String eventPayload;
}