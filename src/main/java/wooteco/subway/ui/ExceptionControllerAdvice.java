package wooteco.subway.ui;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.exception.IllegalSectionException;
import wooteco.subway.exception.NotExistException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(IllegalSectionException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(NotExistException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(EmptyResultDataAccessException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("해당 값이 존재하지 않습니다."));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(DuplicateKeyException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("이미 존재합니다."));
    }
}
