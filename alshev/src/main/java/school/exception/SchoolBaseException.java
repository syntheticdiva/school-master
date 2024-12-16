package school.exception;

public class SchoolBaseException extends RuntimeException {
    public SchoolBaseException(String message) {
        super(message);
    }

    public SchoolBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}