package wooteco.subway.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.constant.BlankArgumentException;
import wooteco.subway.exception.constant.DuplicateException;
import wooteco.subway.exception.constant.NotExistException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotExistException.class)
    private ResponseEntity<Void> handleExceptionToNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({DuplicateException.class, BlankArgumentException.class})
    private ResponseEntity<Void> handleExceptionToBadRequest() {
        return ResponseEntity.badRequest().build();
    }

}
