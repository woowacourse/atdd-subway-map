package wooteco.subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.exception.StationDuplicationException;
import wooteco.subway.exception.StationNotFoundException;

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
}
