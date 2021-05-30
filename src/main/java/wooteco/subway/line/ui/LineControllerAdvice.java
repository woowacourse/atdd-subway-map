package wooteco.subway.line.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.line.exception.LineException;

@ControllerAdvice
public class LineControllerAdvice {
    @ExceptionHandler(LineException.class)
    public ResponseEntity<String> handle(LineException e) {
        return ResponseEntity.status(e.statusCode())
                             .body(e.getMessage());
    }
}
