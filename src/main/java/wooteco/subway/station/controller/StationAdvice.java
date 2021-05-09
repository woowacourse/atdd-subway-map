package wooteco.subway.station.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.exception.NotExistStationException;

@ControllerAdvice
public class StationAdvice {

    @ExceptionHandler(DuplicateStationNameException.class)
    public ResponseEntity<String> handleDuplicateStationNameException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotExistStationException.class)
    public ResponseEntity<String> handleNotExistStationException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
