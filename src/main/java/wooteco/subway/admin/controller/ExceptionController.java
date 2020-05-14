package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.domain.exception.DuplicationNameException;
import wooteco.subway.admin.domain.exception.NotFoundLineException;

@ControllerAdvice
@RestController
public class ExceptionController {
    @ExceptionHandler(value = {NotFoundLineException.class, DuplicationNameException.class})
    public ResponseEntity<String> exceptionHandler(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
