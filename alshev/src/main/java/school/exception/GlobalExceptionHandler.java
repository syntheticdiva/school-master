package school.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.ui.Model;
import org.slf4j.MDC;

import java.util.Date;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import school.dto.ErrorResponse;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "School not found with id: " + ex.getMessage(),
                "Проверьте, существует ли запрашиваемый ресурс."
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }


    @ExceptionHandler(SchoolUpdateException.class)
    public ResponseEntity<String> handleSchoolUpdateException(SchoolUpdateException ex) {
        String message = ex.getMessage() +
                " - Проверьте данные, которые вы пытаетесь обновить.";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationErrors(BindException e, Model model) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return createErrorResponse("Ошибка валидации данных", errorMessage, model);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException e, Model model) {
        return createErrorResponse(
                "Неверный формат параметра",
                "Параметр '" + e.getName() + "' имеет неверный формат",
                model
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMissingParams(MissingServletRequestParameterException e, Model model) {
        return createErrorResponse(
                "Отсутствует обязательный параметр",
                "Параметр '" + e.getParameterName() + "' является обязательным",
                model
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFound(NoHandlerFoundException e, Model model) {
        logError("Страница не найдена", e);
        return createErrorResponse(
                "Страница не найдена",
                "Запрошенная страница '" + e.getRequestURL() + "' не существует",
                model
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllUncaughtException(Exception e, Model model) {
        logError("Произошла системная ошибка", e);
        return createErrorResponse(
                "Произошла системная ошибка",
                "Пожалуйста, попробуйте позже или обратитесь к администратору",
                model
        );
    }

@ExceptionHandler(SchoolNotFoundException.class)
public ResponseEntity<ErrorResponse> handleSchoolNotFoundException(SchoolNotFoundException ex) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "School not found with id: " + ex.getMessage(),
            "Проверьте, существует ли школа с указанным идентификатором."
    );
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
}
    @ExceptionHandler(InvalidSchoolIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSchoolIdException(InvalidSchoolIdException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Убедитесь, что идентификатор является положительным числом."
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ErrorResponse> handleNoContentException(NoContentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NO_CONTENT.value(),
                ex.getMessage(),
                "В базе данных отсутствуют записи о школах"
        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(error);
    }
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        String message = "Ошибка доступа к данным: " + ex.getMessage() +
                " - Убедитесь, что база данных доступна и работает корректно. Проверьте конфигурацию подключения.";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        String message = "Произошла ошибка: " + ex.getMessage() +
                " - Проверьте логи для диагностики проблемы и устраните её.";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message);
    }
    @ExceptionHandler(SchoolServiceException.class)
    public ResponseEntity<ErrorResponse> handleSchoolServiceException(SchoolServiceException ex) {
        log.error("Ошибка сервиса школы: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "School creation error",
                "Не удалось создать школу. " + ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
    @ExceptionHandler(SubscriberNotFoundException.class)
    public ResponseEntity<String> handleSubscriberNotFoundException(SubscriberNotFoundException ex) {
        String message = ex.getMessage() +
                " - Проверьте, существует ли подписчик с указанным идентификатором в базе данных. " +
                "Убедитесь, что идентификатор был введен правильно.";
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(message);
    }
    @ExceptionHandler(SubscriberServiceException.class)
    public ResponseEntity<ErrorResponse> handleSubscriberServiceException(SubscriberServiceException ex) {
        log.error("Ошибка сервиса подписчика: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Subscriber creation error",
                "Не удалось создать подписчика. " + ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(NotificationSendingException.class)
    public ResponseEntity<ErrorResponse> handleNotificationSendingException(NotificationSendingException ex) {
        log.error("Ошибка отправки уведомления: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Notification sending error",
                "Не удалось отправить уведомление. " + ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleRestClientException(RestClientException ex) {
        String message = "Ошибка при отправке уведомления: " + ex.getMessage() +
                " - Проверьте доступность URL подписчика и его корректность." +
                " Убедитесь, что сервис подписчика работает и доступен по сети.";
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(message);
    }
    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<String> handleInterruptedException(InterruptedException ex) {
        String message = "Ошибка при ожидании между попытками отправки уведомления: " + ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message);
    }
    @ExceptionHandler(NotificationProcessingException.class)
    public ResponseEntity<String> handleNotificationProcessingException(NotificationProcessingException ex) {
        String message = ex.getMessage() +
                " - Проверьте логи для получения дополнительной информации о причине ошибки." +
                " Убедитесь, что параметры уведомления корректны и сервис подписчика доступен.";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleEmptyResult(EmptyResultDataAccessException ex) {
        String message = "Подписчики не найдены для указанной школы.";
        log.error("Ошибка: {}", message, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
    @ExceptionHandler(SchoolBaseException.class)
    public ResponseEntity<ErrorResponse> handleSchoolBaseException(SchoolBaseException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Системная ошибка в сервисе школ",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
    @ExceptionHandler(SchoolValidationException.class)
    public ResponseEntity<ErrorResponse> handleSchoolValidationException(SchoolValidationException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации данных школы",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    @ExceptionHandler(DuplicateSchoolException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateSchoolException(DuplicateSchoolException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Школа с указанными параметрами уже существует",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    private String createErrorResponse(String userMessage, String technicalMessage, Model model) {
        model.addAttribute("errorMessage", userMessage);
        model.addAttribute("technicalDetails", technicalMessage);
        model.addAttribute("timestamp", new Date());
        return "error";
    }

    private void logError(String message, Exception e) {
        MDC.put("exceptionClass", e.getClass().getName());
        MDC.put("exceptionMessage", e.getMessage());
        log.error(message);
        MDC.clear();
    }
}