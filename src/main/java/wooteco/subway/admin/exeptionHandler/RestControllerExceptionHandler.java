package wooteco.subway.admin.exeptionHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.dto.ErrorResponse;
import wooteco.subway.admin.error.AlreadyExistException;
import wooteco.subway.admin.error.NotFoundException;

@ControllerAdvice(annotations = RestController.class)
public class RestControllerExceptionHandler {
    @ExceptionHandler({
            IllegalArgumentException.class,
            NotFoundException.class,
            AlreadyExistException.class,
            RuntimeException.class
    })
    public ResponseEntity<ErrorResponse> handle(RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}
