package wooteco.subway.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class StationControllerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity exceptionHandling() {
        return ResponseEntity.badRequest().build();
    }
}
