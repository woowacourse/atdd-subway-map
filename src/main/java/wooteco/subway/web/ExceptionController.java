package wooteco.subway.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.badRequest.BadRequest;
import wooteco.subway.exception.notFound.NotFoundException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity lineNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity lineNameDuplicated(BadRequest e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity argumentNotValid(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getBindingResult());
    }
}