package wooteco.subway.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.exception.sectionexception.SectionAdditionException;
import wooteco.subway.exception.sectionexception.SectionDeleteException;
import wooteco.subway.exception.sectionexception.SectionException;
import wooteco.subway.exception.sectionexception.WrongDistanceException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerHandler {

    @ExceptionHandler(DuplicationException.class)
    public ResponseEntity<String> duplicatedName(DuplicationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> notFoundElement(NotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(SectionException.class)
    public ResponseEntity<String> notFoundElement(SectionException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
