package school.exception;

public class SchoolServiceException extends RuntimeException {
    public SchoolServiceException(String message) {
        super(message);
    }

    public SchoolServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
