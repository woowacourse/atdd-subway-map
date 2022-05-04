package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(final Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException() {
        return ResponseEntity.badRequest().body("정상적인 입력이 아닙니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleServerException(final Exception e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}
