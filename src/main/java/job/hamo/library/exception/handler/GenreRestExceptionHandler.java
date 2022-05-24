package job.hamo.library.exception.handler;

import job.hamo.library.exception.GenreIdAlreadyExistsException;
import job.hamo.library.exception.GenreIdNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GenreRestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({ GenreIdNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Genre not found",
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ GenreIdAlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExistsRequest(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "{\"errors\":{\"genre\": \"genre with UUID already exists\"}}",
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
