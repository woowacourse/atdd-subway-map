package wooteco.subway.controller;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.ElementAlreadyExistException;
import wooteco.subway.exception.IllegalInputException;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<String> illegalInputError(final IllegalInputException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> duplicateNameError(final ElementAlreadyExistException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> noSuchElementError(final NoSuchElementException e) {
        e.printStackTrace();
        return ResponseEntity.notFound().build();
    }
}
