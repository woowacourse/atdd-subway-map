package wooteco.subway.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicationException;

@ControllerAdvice
public class ControllerHandler {

    @ExceptionHandler(DuplicationException.class)
    public ResponseEntity<String> duplicatedName(DuplicationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
