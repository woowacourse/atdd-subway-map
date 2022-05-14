package wooteco.subway.error;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdviser {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> duplicateKeyExceptionHandler(DuplicateKeyException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("테이블 유니크 값 중복", e.getMessage()));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> EmptyResultDataAccessExceptionHandler(EmptyResultDataAccessException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("존재하지 않는 PK", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> IllegalArgumentExceptionHandler(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("잘못 된 요청값", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(RuntimeException e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(new ErrorResponse("런타임 에러", "현재 서버에 문제가 발생해 응답할 수 없습니다."));
    }
}
