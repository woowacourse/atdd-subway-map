package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.ClientException;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.DataNotExistException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({DuplicateNameException.class})
    public ResponseEntity<String> handleDuplicateNameException(ClientException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler({DataNotExistException.class})
    public ResponseEntity<String> handleNoDataExistException(ClientException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
