package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NoSuchContentException;
import wooteco.subway.exception.SubwayException;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<String> handleDuplicateLineNameException(DuplicateNameException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<String> handleSubwayException(SubwayException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
