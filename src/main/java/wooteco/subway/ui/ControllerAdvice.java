package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.ClientException;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.DataNotFoundException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({DuplicateNameException.class})
    public ResponseEntity<String> handleDuplicateNameException(ClientException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler({DataNotFoundException.class})
    public ResponseEntity<String> handleDataNotFoundException(ClientException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
