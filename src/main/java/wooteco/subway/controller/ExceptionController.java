package wooteco.subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.NotAddableSectionException;
import wooteco.subway.exception.NotRemovableException;
import wooteco.subway.exception.NotRemovableSectionException;
import wooteco.subway.exception.SameStationSectionException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> titleNotValidException(MethodArgumentNotValidException error) {
        return ResponseEntity.badRequest().body(error.getBindingResult().getAllErrors().get(0).getDefaultMessage());
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
    public ResponseEntity<String> handleNotAddableSectionException(NotAddableSectionException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotRemovableException.class)
    public ResponseEntity<String> handleNotRemovableException(NotRemovableException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
