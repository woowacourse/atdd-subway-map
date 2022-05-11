package wooteco.subway.ui.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.dto.response.ExceptionResponse;

@RestControllerAdvice
public class ExceptionAdviser {

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<ExceptionResponse> handle(Exception exception) {
        return ResponseEntity.badRequest().body(ExceptionResponse.of(exception));
    }
}