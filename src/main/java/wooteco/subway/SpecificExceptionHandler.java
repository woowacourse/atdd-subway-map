package wooteco.subway;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.repository.DataNotFoundException;
import wooteco.subway.exception.repository.DuplicatedFieldException;
import wooteco.subway.exception.service.ObjectNotFoundException;
import wooteco.subway.exception.service.ValidationFailureException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SpecificExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> handleDataNotFoundException(final DataNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(DuplicatedFieldException.class)
    public ResponseEntity<String> handleDuplicatedFieldException(final DuplicatedFieldException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<String> handleObjectNotFoundException(final ObjectNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ValidationFailureException.class)
    public ResponseEntity<String> handleValidationFailureException(final ValidationFailureException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
