package wooteco.subway;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicatedNameException;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(DuplicatedNameException.class)
    public ResponseEntity<Void> duplicatedException() {
        return ResponseEntity.badRequest().build();
    }
}
