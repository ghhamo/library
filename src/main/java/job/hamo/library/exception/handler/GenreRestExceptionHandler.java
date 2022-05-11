package job.hamo.library.exception.handler;

import job.hamo.library.exception.GenreUUIDAlreadyExistsException;
import job.hamo.library.exception.GenreUUIDNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GenreRestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({ GenreUUIDNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Genre not found",
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ GenreUUIDAlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExistsRequest(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "{\"errors\":{\"genre\": \"genre with UUID already exists\"}}",
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
