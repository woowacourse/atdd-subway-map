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
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handle(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = AlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handle(AlreadyExistException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}
