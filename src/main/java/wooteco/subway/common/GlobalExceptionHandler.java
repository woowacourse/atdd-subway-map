package wooteco.subway.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.line.exception.ClientRuntimeException;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicatedNameException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientRuntimeException.class)
    public ResponseEntity<String> handleClientException(final ClientRuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DuplicatedNameException.class)
    public ResponseEntity<String> duplicatedNameExceptionHandler(final DuplicatedNameException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> dataNotFoundExceptionHandler(final DataNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleServerException(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
