package wooteco.subway.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.BadRequestException;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Void> handleException(BadRequestException e) {
        return ResponseEntity.badRequest().build();
    }
}
