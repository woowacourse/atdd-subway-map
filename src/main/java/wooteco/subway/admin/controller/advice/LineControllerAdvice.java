package wooteco.subway.admin.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.exception.ExistingNameException;

@RestControllerAdvice
public class LineControllerAdvice {
    @ExceptionHandler(ExistingNameException.class)
    public ResponseEntity<String> catchExistingNameException(ExistingNameException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
