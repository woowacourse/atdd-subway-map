package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorResponse;

@ControllerAdvice(assignableTypes = {StationController.class, LineController.class})
public class ExceptionAdvice {

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }
}
