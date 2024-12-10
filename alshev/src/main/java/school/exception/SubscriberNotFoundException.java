package school.exception;

public class SubscriberNotFoundException extends RuntimeException {
    public SubscriberNotFoundException(String message) {
        super(message);
    }

    public SubscriberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
