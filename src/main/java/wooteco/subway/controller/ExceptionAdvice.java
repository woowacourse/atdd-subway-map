package wooteco.subway.controller;

import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, NoSuchElementException.class})
    public ResponseEntity<String> clientError(final Exception exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler()
    public ResponseEntity<String> unexpectedError(Exception exception) {
        exception.printStackTrace();
        return ResponseEntity.internalServerError().body("[ERROR] 예기치 못한 에러가 발생했습니다.");
    }
}
