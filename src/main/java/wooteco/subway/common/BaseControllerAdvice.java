package wooteco.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.line.exception.LineExistenceException;
import wooteco.subway.station.exception.StationExistenceException;

@ControllerAdvice
public class BaseControllerAdvice {
    @ExceptionHandler({StationExistenceException.class, LineExistenceException.class})
    public ResponseEntity<ExceptionMessageObj> duplicateNameExceptionHandling(Exception exception) {
        return ResponseEntity.badRequest().body(new ExceptionMessageObj(exception.getMessage()));
    }
}
