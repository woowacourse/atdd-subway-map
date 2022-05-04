package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.BlankArgumentException;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotExistStationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DuplicateNameException.class, BlankArgumentException.class})
    private ResponseEntity<Void> handleExceptionToBadRequest() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NotExistStationException.class)
    private ResponseEntity<Void> handleExceptionToNotFound() {
        return ResponseEntity.notFound().build();
    }

}
