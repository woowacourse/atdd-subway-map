package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Void> illegalStateExceptionHandler(Exception exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> noSuchElementExceptionHandler(Exception exception) {
        return ResponseEntity.noContent().build();
    }
}
