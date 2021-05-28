package wooteco.subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.duplicateException.LineDuplicationException;
import wooteco.subway.exception.duplicateException.StationDuplicationException;
import wooteco.subway.exception.notAddableSectionException.NotAddableSectionException;
import wooteco.subway.exception.notFoundException.NotFoundException;
import wooteco.subway.exception.notRemovableException.NotRemovableException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
            .body(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler({LineDuplicationException.class, StationDuplicationException.class})
    public ResponseEntity<String> handleNameDuplicationException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
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
