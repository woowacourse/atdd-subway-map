package wooteco.subway.controller;

import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.DuplicateNameException;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<String> illegalArgument(final Exception exception) {
        exception.printStackTrace();
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElement(final Exception exception) {
        exception.printStackTrace();
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler()
    public ResponseEntity<String> unexpectedError(final Exception exception) {
        exception.printStackTrace();
        return ResponseEntity.internalServerError().body("[ERROR] 예기치 못한 에러가 발생했습니다.");
    }
}
