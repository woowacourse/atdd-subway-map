package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Void> error() {
        return ResponseEntity.badRequest().build();
    }
}
