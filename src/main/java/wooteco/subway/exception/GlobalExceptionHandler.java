package wooteco.subway.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(final Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(EmptyArgumentException.class)
    public ResponseEntity<ErrorResponse> emptyArgumentException(final Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> parameterException() {
        return ResponseEntity.badRequest().body(new ErrorResponse("입력하신 정보가 요구조건에 맞지 않습니다."));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> duplicateException() {
        return ResponseEntity.badRequest().body(new ErrorResponse("중복된 정보입니다."));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> emptyResultException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("존재하지 않은 정보입니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception() {
        return ResponseEntity.internalServerError().body(new ErrorResponse("서버 에러가 발생했습니다."));
    }
}
