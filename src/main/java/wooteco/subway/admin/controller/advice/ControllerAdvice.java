package wooteco.subway.admin.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.exception.ExistingNameException;
import wooteco.subway.admin.exception.LineStationException;
import wooteco.subway.admin.exception.NotFoundException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(ExistingNameException.class)
    public ResponseEntity<String> existingNameException(ExistingNameException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> notFoundException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(LineStationException.class)
    public ResponseEntity<String> lineStationException(LineStationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

}
