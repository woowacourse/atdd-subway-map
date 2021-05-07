package wooteco.subway.exception.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.exception.DuplicateNameException;

@RestControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(value = DuplicateNameException.class)
    public ResponseEntity<Void> duplicateNameExceptionHandler() {
        return ResponseEntity.badRequest().build();
    }
}
