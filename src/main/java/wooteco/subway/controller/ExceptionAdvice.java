package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.WebException;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(WebException.class)
    public ResponseEntity<String> handle(WebException e) {
        return new ResponseEntity(e.getBody(), e.getHttpStatus());
    }
}
