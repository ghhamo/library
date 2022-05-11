package job.hamo.library.exception.handler;

import job.hamo.library.exception.AuthorUUIDAlreadyExistsException;
import job.hamo.library.exception.AuthorUUIDNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AuthorRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ AuthorUUIDNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "{\"errors\":{\"authorName\": \"Author not found\"}}",
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({AuthorUUIDAlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExistsRequest(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "{\"errors\":{\"authorAddress\": \"Author with address already exists\"}}",
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}