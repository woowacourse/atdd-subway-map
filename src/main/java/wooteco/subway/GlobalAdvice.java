package wooteco.subway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.RequestException;

@ControllerAdvice
public final class GlobalAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleUnknownException(final RuntimeException e) {
        final String message = "unhandled exceptions";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    @ExceptionHandler(RequestException.class)
    public ResponseEntity<String> handleIllegalArgumentException(final RequestException e) {
        return ResponseEntity.status(e.status()).body(e.getMessage());
    }
}
