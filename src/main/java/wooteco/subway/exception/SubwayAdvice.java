package wooteco.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class SubwayAdvice {

    @ExceptionHandler(DuplicateStationNameException.class)
    public ResponseEntity<String> handleDuplicateStationNameException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotExistStationException.class)
    public ResponseEntity<String> handleNotExistStationException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DuplicateLineNameException.class)
    public ResponseEntity<String> handleDuplicateLineNameException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotExistLineException.class)
    public ResponseEntity<String> handleNotExistLineException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotAddSectionException.class)
    public ResponseEntity<String> handleNotAddSectionException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotContainStationsException.class)
    public ResponseEntity<String> handleNotContainStationsException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotExistSectionException.class)
    public ResponseEntity<String> handleNotExistSectionException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundTerminalStationException.class)
    public ResponseEntity<String> handleNotFoundTerminalStationException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
