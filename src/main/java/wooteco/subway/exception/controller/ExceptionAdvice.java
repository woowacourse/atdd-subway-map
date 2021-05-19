package wooteco.subway.exception.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.InvalidInsertException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.exception.dto.ExceptionResponse;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(DuplicatedNameException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicatedNameException(DuplicatedNameException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(InvalidInsertException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidInsertException(InvalidInsertException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionResponse> databaseErrorLog(DataAccessException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }
}
