package wooteco.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.SubwayException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<String> handle(SubwayException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
