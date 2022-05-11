package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.AddSectionException;
import wooteco.subway.exception.ClientException;
import wooteco.subway.exception.DeleteSectionException;
import wooteco.subway.exception.datanotfound.DataNotFoundException;
import wooteco.subway.exception.duplicatename.DuplicateNameException;

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

    @ExceptionHandler({AddSectionException.class})
    public ResponseEntity<String> handleAddSectionException(ClientException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler({DeleteSectionException.class})
    public ResponseEntity<String> handleDeleteSectionException(ClientException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
