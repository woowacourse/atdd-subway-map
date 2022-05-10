package wooteco.subway.ui;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvisor {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicateKeyException() {
        return ResponseEntity.badRequest().body("이름은 중복될 수 없습니다.");
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> handleEmptyResultDataAccessException() {
        return ResponseEntity.badRequest().body("존재하지 않는 결과입니다.");
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<String> handleException() {
        return ResponseEntity.internalServerError().body("예상치 못한 에러가 발생했습니다.");
    }
}
