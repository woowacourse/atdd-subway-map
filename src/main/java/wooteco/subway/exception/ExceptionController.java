package wooteco.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handle(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<String> handle(SubwayException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }


}
