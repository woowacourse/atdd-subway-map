package wooteco.subway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.SubwayException;

@RestControllerAdvice
public class ExceptionAdvice {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<Object> handle(SubwayException e) {
        logger.error(String.valueOf(e.getBody()));
        return ResponseEntity.status(e.getHttpStatus()).body(e.getBody());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDaoException(DuplicateKeyException e) {
        logger.error(String.valueOf(e.getMessage()));
        return ResponseEntity.badRequest().body("중복된 값이 존재합니다.");
    }
}
