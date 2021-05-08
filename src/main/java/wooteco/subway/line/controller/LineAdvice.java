package wooteco.subway.line.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.NotExistLineException;

@ControllerAdvice
public class LineAdvice {

    @ExceptionHandler(DuplicateLineNameException.class)
    public ResponseEntity<String> handleDuplicateLineNameException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotExistLineException.class)
    public ResponseEntity<String> handleNotExistLineException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
