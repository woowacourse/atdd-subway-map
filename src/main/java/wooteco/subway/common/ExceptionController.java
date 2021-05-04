package wooteco.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.LineNotFoundException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity lineNotFound() {
        return ResponseEntity.notFound().build();
    }
}


