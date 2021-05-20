package wooteco.subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.NoSuchDataException;

@ControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(NoSuchDataException.class)
    public ResponseEntity handleNameDuplication() {
        return ResponseEntity.badRequest().build();
    }
}
