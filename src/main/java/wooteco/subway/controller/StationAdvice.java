package wooteco.subway.controller;

import java.util.NoSuchElementException;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StationAdvice {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Void> handle(Exception e) {
        return ResponseEntity.badRequest().build();
    }
}
