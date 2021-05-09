package wooteco.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.line.LineNameDuplicatedException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.exception.station.StationNameDuplicatedException;
import wooteco.subway.exception.station.StationNotFoundException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({StationNotFoundException.class, LineNotFoundException.class})
    public ResponseEntity<Void> lineNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({LineNameDuplicatedException.class, StationNameDuplicatedException.class})
    public ResponseEntity<String> lineNameDuplicated(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


