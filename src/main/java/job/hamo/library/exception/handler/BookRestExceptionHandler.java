package job.hamo.library.exception.handler;

import job.hamo.library.exception.BookNotFoundInCollectionException;
import job.hamo.library.exception.BookIdAlreadyExistsException;
import job.hamo.library.exception.BookIdNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class BookRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ BookIdNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Book not found", 
          new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    @ExceptionHandler({ BookNotFoundInCollectionException.class })
    protected ResponseEntity<Object> handleNotInCollectionFound(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Book not found in collection",
          new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ BookIdAlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExistsRequest(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "{\"errors\":{\"bookTitle\": \"Book already exists\"}}",
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}