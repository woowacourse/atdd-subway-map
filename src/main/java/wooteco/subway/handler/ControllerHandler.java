package wooteco.subway.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicatedNameException;

@ControllerAdvice
public class ControllerHandler {

    @ExceptionHandler(DuplicatedNameException.class)
    public ResponseEntity<String> duplicatedName(DuplicatedNameException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
