package school.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("School not found");
        String viewName = exceptionHandler.handleResourceNotFoundException(ex, model);

        assertEquals("error", viewName);
        verify(model).addAttribute(eq("errorMessage"), eq("Школа не найдена"));
        verify(model).addAttribute(eq("technicalDetails"), eq("School not found"));
        verify(model).addAttribute(eq("timestamp"), any(java.util.Date.class));
    }

    @Test
    void handleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");

        String viewName = exceptionHandler.handleTypeMismatch(ex, model);

        assertEquals("error", viewName);
        verify(model).addAttribute(eq("errorMessage"), eq("Неверный формат параметра"));
        verify(model).addAttribute(eq("technicalDetails"), eq("Параметр 'id' имеет неверный формат"));
        verify(model).addAttribute(eq("timestamp"), any(java.util.Date.class));
    }

    @Test
    void handleMissingParams() {
        MissingServletRequestParameterException ex = mock(MissingServletRequestParameterException.class);
        when(ex.getParameterName()).thenReturn("name");

        String viewName = exceptionHandler.handleMissingParams(ex, model);

        assertEquals("error", viewName);
        verify(model).addAttribute(eq("errorMessage"), eq("Отсутствует обязательный параметр"));
        verify(model).addAttribute(eq("technicalDetails"), eq("Параметр 'name' является обязательным"));
        verify(model).addAttribute(eq("timestamp"), any(java.util.Date.class));
    }

    @Test
    void handleNoHandlerFound() {
        NoHandlerFoundException ex = mock(NoHandlerFoundException.class);
        when(ex.getRequestURL()).thenReturn("/unknown");

        String viewName = exceptionHandler.handleNoHandlerFound(ex, model);

        assertEquals("error", viewName);
        verify(model).addAttribute(eq("errorMessage"), eq("Страница не найдена"));
        verify(model).addAttribute(eq("technicalDetails"), eq("Запрошенная страница '/unknown' не существует"));
        verify(model).addAttribute(eq("timestamp"), any(java.util.Date.class));
    }

    @Test
    void handleAllUncaughtException() {
        Exception ex = new RuntimeException("Unexpected error");

        String viewName = exceptionHandler.handleAllUncaughtException(ex, model);

        assertEquals("error", viewName);
        verify(model).addAttribute(eq("errorMessage"), eq("Произошла непредвиденная ошибка"));
        verify(model).addAttribute(eq("technicalDetails"), eq("Пожалуйста, попробуйте позже или обратитесь к администратору"));
        verify(model).addAttribute(eq("timestamp"), any(java.util.Date.class));
    }
}