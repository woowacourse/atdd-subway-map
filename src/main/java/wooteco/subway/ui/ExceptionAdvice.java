package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.NameDuplicationException;

@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(NameDuplicationException.class)
    public ResponseEntity<Void> duplicatedNameFound() {
        return ResponseEntity.badRequest().build();
    }
}
