package dev.nano.herbclassifier.common.exception;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.resolve;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlingController extends ResponseEntityExceptionHandler implements ErrorController {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                ExceptionHttpResponse.builder()
                        .reason(exception.getMessage())
                        .message(exception.getMessage())
                        .status(resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .developerMessage(exception.getMessage())
                        .build(), statusCode);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ExceptionHttpResponse> servletException(ServletException exception) {
        return createHttpErrorResponse(INTERNAL_SERVER_ERROR, exception.getMessage(), exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionHttpResponse> exception(Exception exception) {
        return createHttpErrorResponse(INTERNAL_SERVER_ERROR, exception.getMessage(), exception);
    }

    private ResponseEntity<ExceptionHttpResponse> createHttpErrorResponse(HttpStatus httpStatus, String reason, Exception exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                ExceptionHttpResponse.builder()
                        .reason(reason)
                        .status(httpStatus)
                        .statusCode(httpStatus.value())
                        .developerMessage(exception.getMessage())
                        .build(), httpStatus);
    }
}
