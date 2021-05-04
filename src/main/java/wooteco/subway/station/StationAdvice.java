package wooteco.subway.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicateException;

@ControllerAdvice
public class StationAdvice {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<String> handleDuplicateException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
