package wooteco.subway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.WebException;

@RestControllerAdvice
public class ExceptionAdvice {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(WebException.class)
    public ResponseEntity<Object> handle(WebException e) {
        logger.error(String.valueOf(e.getBody()));
        return ResponseEntity.status(e.getHttpStatus()).body(e.getBody());
    }
}
