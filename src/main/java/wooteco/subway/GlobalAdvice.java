package wooteco.subway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.station.exception.StationException;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleUnknownException() {
        final String message = "unhandled exceptions";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleInvalidRequest(final BindingResult bindingResult) {
        FieldError fieldError = bindingResult.getFieldError();
        return ResponseEntity.badRequest().body(fieldError.getDefaultMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleInvalidRequest(final ConstraintViolationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({StationException.class, LineException.class})
    public ResponseEntity<String> handleStationException(final RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
