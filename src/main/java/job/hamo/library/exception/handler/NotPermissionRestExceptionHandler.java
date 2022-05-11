package job.hamo.library.exception.handler;


import job.hamo.library.exception.NotPermissionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class NotPermissionRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NotPermissionException.class})
    public ResponseEntity<Object> handleAlreadyExistsRequest(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "{\"errors\":{\"userEmail\": \"you havn't permission do that action\"}}",
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
