package wooteco.subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.exception.NotFoundStationException;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(DuplicatedNameException.class)
    public ResponseEntity<Void> duplicatedException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NotFoundStationException.class)
    public ResponseEntity<Void> notFoundStationException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity<Void> notFoundLineException() {
        return ResponseEntity.badRequest().build();
    }
}
