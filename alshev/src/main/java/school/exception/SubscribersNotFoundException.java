package school.exception;

public class SubscribersNotFoundException extends RuntimeException {
    public SubscribersNotFoundException(String message) {
        super(message);
    }
}
