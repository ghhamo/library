package job.hamo.library.exception.handler;

import job.hamo.library.exception.DownloadFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DownloadFailedRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({DownloadFailedException.class})
    public ResponseEntity<Object> handleAlreadyExistsRequest(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "{\"errors\":{\"download\": \"downloading failed\"}}",
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
