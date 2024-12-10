package school.exception;

public class SubscriberServiceException extends RuntimeException {
    public SubscriberServiceException(String message) {
        super(message);
    }

    public SubscriberServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}