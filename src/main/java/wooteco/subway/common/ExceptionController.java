package wooteco.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.exception.SubwayException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity lineNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity lineNameDuplicated(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


