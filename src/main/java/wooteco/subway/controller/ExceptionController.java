package wooteco.subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exceptions.LineDuplicationException;
import wooteco.subway.exceptions.LineNotFoundException;
import wooteco.subway.exceptions.StationDuplicationException;
import wooteco.subway.exceptions.StationNotFoundException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> exception(Exception exception) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({LineDuplicationException.class, StationDuplicationException.class})
    public ResponseEntity<String> nameDuplicationException(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({LineNotFoundException.class, StationNotFoundException.class})
    public ResponseEntity<String> notFoundException(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
