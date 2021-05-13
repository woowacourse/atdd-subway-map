package wooteco.subway.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.domain.exception.DomainException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomainException(Exception e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> handleEmptyResultException(Exception e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}
