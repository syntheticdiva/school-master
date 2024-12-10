package school.exception;

public class NotificationStatusPersistenceException extends RuntimeException {
    public NotificationStatusPersistenceException(String message) {
        super(message);
    }

    public NotificationStatusPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
