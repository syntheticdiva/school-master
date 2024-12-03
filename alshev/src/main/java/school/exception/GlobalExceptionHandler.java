package school.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.ui.Model;
import org.slf4j.MDC;

import java.util.Date;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException e, Model model) {
        return createErrorResponse("Школа не найдена", e.getMessage(), model);
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