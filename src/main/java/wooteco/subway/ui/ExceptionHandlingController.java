package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundIdException;

@RestControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(NotFoundIdException.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<String> handleDuplicateName(DuplicateNameException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
