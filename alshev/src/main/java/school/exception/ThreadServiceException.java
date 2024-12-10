package school.exception;

public class ThreadServiceException extends RuntimeException {
    public ThreadServiceException(String message) {
        super(message);
    }

    public ThreadServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}