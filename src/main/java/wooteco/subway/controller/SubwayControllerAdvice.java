package wooteco.subway.controller;

import java.util.NoSuchElementException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
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

    @ExceptionHandler({NoHandlerFoundException.class, NoSuchElementException.class})
    public ResponseEntity<Void> handleNoHandlerFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Void> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Void> handleDataAccessException() {
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Void> handleException() {
        return ResponseEntity.internalServerError().build();
    }
}
