package wooteco.subway.station.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.station.exception.StationException;

@ControllerAdvice
public class StationControllerAdvice {
    @ExceptionHandler(StationException.class)
    public ResponseEntity<String> handle(StationException e) {
        return ResponseEntity.status(e.statusCode())
                             .body(e.getMessage());
    }
}
