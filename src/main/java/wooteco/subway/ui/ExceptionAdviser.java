package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.dto.ExceptionResponse;
import wooteco.subway.exception.CustomException;

@RestControllerAdvice
public class ExceptionAdviser {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handle(CustomException exception) {
        return ResponseEntity.badRequest().body(ExceptionResponse.of(exception));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handler(RuntimeException exception) {
        return ResponseEntity.internalServerError().body(ExceptionResponse.of(exception));
    }

}
