package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.dto.ExceptionResponse;

@RestControllerAdvice
public class ExceptionAdviser {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handle(IllegalStateException exception) {
        return ResponseEntity.badRequest().body(ExceptionResponse.of(exception));
    }
}
