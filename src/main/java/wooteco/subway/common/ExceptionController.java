package wooteco.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.SubwayException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({SubwayException.class})
    public ResponseEntity<String> lineNameDuplicated(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


