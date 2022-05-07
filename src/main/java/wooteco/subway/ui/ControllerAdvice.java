package wooteco.subway.ui;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({IllegalStateException.class, NoSuchElementException.class})
    public ResponseEntity<String> exception(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
