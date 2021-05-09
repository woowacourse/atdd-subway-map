package wooteco.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.line.exception.LineExistenceException;
import wooteco.subway.station.exception.StationExistenceException;

@ControllerAdvice
public class BaseControllerAdvice {
    @ExceptionHandler({StationExistenceException.class, LineException.class})
    public ResponseEntity<ExceptionMessageObj> lineAndStationExceptionHandling(Exception exception) {
        return ResponseEntity.badRequest().body(new ExceptionMessageObj(exception.getMessage()));
    }
}
