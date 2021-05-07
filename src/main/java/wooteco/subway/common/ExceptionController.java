package wooteco.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicatedLineInformationException;
import wooteco.subway.exception.InsufficientLineInformationException;
import wooteco.subway.exception.StationNotFoundException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({
            DuplicatedLineInformationException.class,
            InsufficientLineInformationException.class,
            StationNotFoundException.class
    })
    public ResponseEntity<String> handle(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
