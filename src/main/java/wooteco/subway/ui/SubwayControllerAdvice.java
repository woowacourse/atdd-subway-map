package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.line.DuplicateLineNameException;
import wooteco.subway.exception.NoSuchContentException;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NoSuchContentException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchContentException e) {
        return ResponseEntity.status(e.getStatus())
                .body(e.getMessage());
    }

    @ExceptionHandler(DuplicateLineNameException.class)
    public ResponseEntity<String> handDuplicateLineNameException(DuplicateLineNameException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
