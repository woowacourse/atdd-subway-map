package wooteco.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.exception.ClientException;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class StationControllerAdvice {

    @ExceptionHandler({ClientException.class})
    public ResponseEntity<String> IllegalArgumentExceptionHandle(ClientException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<String> ConstraintViolationExceptionHandle(ClientException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
