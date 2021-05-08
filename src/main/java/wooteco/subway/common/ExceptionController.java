package wooteco.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.exception.line.LineNotFoundException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<String> handle(LineNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<String> handle(SubwayException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }


}
