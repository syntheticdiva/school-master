package school.exception;

public class SchoolUpdateException extends RuntimeException {
    public SchoolUpdateException(String message) {
        super(message);
    }

    public SchoolUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}