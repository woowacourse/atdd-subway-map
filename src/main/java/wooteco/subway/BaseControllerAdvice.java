package wooteco.subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.line.LineExistenceException;
import wooteco.subway.station.StationExistenceException;

@ControllerAdvice
public class BaseControllerAdvice {
    @ExceptionHandler(StationExistenceException.class)
    public ResponseEntity duplicateStationNameExceptionHandling(Exception exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(LineExistenceException.class)
    public ResponseEntity duplicateLineNameExceptionHandling(Exception exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
