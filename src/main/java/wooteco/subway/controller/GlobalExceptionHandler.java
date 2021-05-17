package wooteco.subway.controller;

import javax.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.controller.dto.response.LineResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<LineResponse> exceptionHandler(IllegalArgumentException e) {
        System.out.println("@@@@@ " + "IllegalArgument 걸림");
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Void> validationHandler(ConstraintViolationException e) {
        System.out.println("@@@@@ " + "Validation 걸림");
        return ResponseEntity.badRequest().build();
    }
}
