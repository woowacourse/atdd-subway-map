package wooteco.subway.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.domain.exception.DomainException;
import wooteco.subway.service.exception.ServiceException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomainException(DomainException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<String> handleDomainException(ServiceException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> handleEmptyResultException(EmptyResultDataAccessException exception) {
        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }
}
