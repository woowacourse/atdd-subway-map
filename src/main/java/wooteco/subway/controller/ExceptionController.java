package wooteco.subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exceptions.LineDuplicationException;
import wooteco.subway.exceptions.LineNotFoundException;
import wooteco.subway.exceptions.NotAddableSectionException;
import wooteco.subway.exceptions.StationDuplicationException;
import wooteco.subway.exceptions.StationNotFoundException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> exception(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler({LineDuplicationException.class, StationDuplicationException.class})
    public ResponseEntity<String> nameDuplicationException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler({LineNotFoundException.class, StationNotFoundException.class})
    public ResponseEntity<String> notFoundException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(NotAddableSectionException.class)
    public ResponseEntity<String> handleNotAddableSectionException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
