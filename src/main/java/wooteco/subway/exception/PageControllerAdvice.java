package wooteco.subway.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PageControllerAdvice {
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<String> duplicatedExceptionHandle(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NoSuchException.class)
    public ResponseEntity<String> noSuchExceptionHandle(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalMethodException.class)
    public ResponseEntity<String> illegalMethodExceptionHandle(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Void> runtimeExceptionHandle() {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
    }
}
