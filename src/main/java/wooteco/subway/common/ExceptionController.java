package wooteco.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.LineNameDuplicatedException;
import wooteco.subway.exception.LineNotFoundException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<String> lineNotFound(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(LineNameDuplicatedException.class)
    public ResponseEntity<String> lineNameDuplicated(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


