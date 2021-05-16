package wooteco.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handle(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handle(InvalidRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InternalLogicConflictException.class)
    public ResponseEntity<Void> handle(InternalLogicConflictException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
