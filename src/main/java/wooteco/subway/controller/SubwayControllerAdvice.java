package wooteco.subway.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import wooteco.subway.dto.SubwayErrorResponse;
import wooteco.subway.exception.NotFoundException;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<SubwayErrorResponse> handleBusinessException(IllegalStateException exception) {
        return ResponseEntity.badRequest().body(SubwayErrorResponse.from(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SubwayErrorResponse> handleMethodValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(SubwayErrorResponse.from(exception));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Void> handleNoHandlerFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<SubwayErrorResponse> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SubwayErrorResponse.from(exception));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Void> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Void> handleDataAccessException() {
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException() {
        return ResponseEntity.internalServerError().build();
    }
}
