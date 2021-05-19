package wooteco.subway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.BusinessException;

@ControllerAdvice
public final class GlobalAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleUnknownException() {
        final String message = "unhandled exceptions";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(final BusinessException e) {
        return ResponseEntity.status(e.status()).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException() {
        final String message = "요청 값이 올바르지 않습니다.";
        return ResponseEntity.badRequest().body(message);
    }
}
