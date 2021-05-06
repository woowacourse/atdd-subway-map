package wooteco.subway;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Void> duplicatedException() {
        return ResponseEntity.badRequest().build();
    }
}
