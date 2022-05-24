package job.hamo.library.exception.handler;

import job.hamo.library.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class BookListRestExceptionHandler extends ResponseEntityExceptionHandler{
    @ExceptionHandler({ BookListIdNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Collection not found",
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ BookListIdAlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExistsRequest(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "{\"errors\":{\"listName\": \"List with name already exists\"}}",
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
