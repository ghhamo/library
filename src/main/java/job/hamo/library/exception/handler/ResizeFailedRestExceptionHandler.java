package job.hamo.library.exception.handler;

import job.hamo.library.exception.ResizeFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ResizeFailedRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResizeFailedException.class})
    public ResponseEntity<Object> handleAlreadyExistsRequest(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "{\"errors\":{\"image\": \"resizing failed\"}}",
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
