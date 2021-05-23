package wooteco.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<String> handleDuplicateException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

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

    @ExceptionHandler(InvalidSectionsException.class)
    public ResponseEntity<String> handleInvalidSectionsException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotRemoveSectionException.class)
    public ResponseEntity<String> handleNotRemoveSectionException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(SectionDistanceException.class)
    public ResponseEntity<String> handleSectionDistanceException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DeleteSectionException.class)
    public ResponseEntity<String> handleDeleteSectionException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
