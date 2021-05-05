package wooteco.subway;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity duplicatedException() {
        return ResponseEntity.badRequest().build();
    }
}
