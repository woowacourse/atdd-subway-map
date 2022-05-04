package wooteco.subway.ui.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> parameterException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Void> duplicateException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Void> emptyResultException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> exception() {
        return ResponseEntity.internalServerError().build();
    }
}
