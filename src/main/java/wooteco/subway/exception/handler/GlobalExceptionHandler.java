package wooteco.subway.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.response.ErrorResponse;
import wooteco.subway.exception.duplicate.DuplicateException;
import wooteco.subway.exception.notfound.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> parameterException(final Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> duplicateException(final Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponse> emptyResultException(final Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception() {
        return ResponseEntity.internalServerError().body(new ErrorResponse("오류가 발생했습니다. 관리자에게 문의해주세요*^^*"));
    }
}
