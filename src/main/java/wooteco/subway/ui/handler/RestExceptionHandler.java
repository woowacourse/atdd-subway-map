package wooteco.subway.ui.handler;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicateKey() {
        return ResponseEntity.badRequest().body("이름 혹은 색은 중복될 수 없습니다.");
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> handleDataNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 데이터를 조회할 수 없습니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleInvalidArguments() {
        return ResponseEntity.badRequest().body("이름은 공백일 수 없습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedException() {
        return ResponseEntity.internalServerError().body("서버에서 예상하지 못한 문제가 발생했습니다.");
    }
}
