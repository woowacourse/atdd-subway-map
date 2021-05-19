package wooteco.subway;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicatedFieldException;
import wooteco.subway.exception.ValidationFailureException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SpecificExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleRequestValidationException(final MethodArgumentNotValidException e) {
        final StringBuffer stringBuffer = new StringBuffer();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            final String fieldName = ((FieldError) error).getField();
            final String errorMessage = error.getDefaultMessage();
            stringBuffer.append(String.format("%s 검증 실패: %s %n", fieldName, errorMessage));
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(stringBuffer.toString());
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> handleDataNotFoundException(final DataNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(DuplicatedFieldException.class)
    public ResponseEntity<String> handleDuplicatedFieldException(final DuplicatedFieldException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ValidationFailureException.class)
    public ResponseEntity<String> handleValidationFailureException(final ValidationFailureException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
