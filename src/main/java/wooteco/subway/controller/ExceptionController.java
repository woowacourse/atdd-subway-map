package wooteco.subway.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> illegalArgumentException(DuplicateKeyException exception) {
        return ResponseEntity.badRequest().body("중복된 값을 입력할 수 없습니다.");
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Void> emptyResultDataAccessException(
        EmptyResultDataAccessException error) {
        return ResponseEntity.badRequest().build();
    }
}
