package wooteco.subway.controller;

import java.util.NoSuchElementException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import wooteco.subway.dto.SubwayErrorResponse;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<SubwayErrorResponse> handleBusinessException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(SubwayErrorResponse.from(exception));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NoHandlerFoundException");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NoSuchElementException");
    }


    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> handleNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EmptyResultDataAccessException");

    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DataAccessException");

    }
}
