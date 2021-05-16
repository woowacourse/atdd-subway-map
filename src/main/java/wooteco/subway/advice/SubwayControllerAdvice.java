package wooteco.subway.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class SubwayControllerAdvice {
    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<Map<String, String>> duplicateExceptionHandler(final DuplicateNameException e) {
        Map<String, String> body = new HashMap<>();
        body.put("Error message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> notFoundExceptionHandler(final NotFoundException e) {
        Map<String, String> body = new HashMap<>();
        body.put("Error message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> emptyValueExceptionHandler(final MethodArgumentNotValidException e) {
        Map<String, String> body = new HashMap<>();
        body.put("Error message", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> anOtherExceptionHandler(final Exception e) {
        Map<String, String> body = new HashMap<>();
        body.put("Error message", e.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}
