package wooteco.subway.station.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.station.DuplicatedStationTitleException;
import wooteco.subway.exception.station.NotFoundStationException;

@RestControllerAdvice
public class StationControllerAdvice {

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity overlappedStationExceptionResponse() {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(NotFoundStationException.class)
    public ResponseEntity voidStationExceptionResponse() {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(DuplicatedStationTitleException.class)
    public ResponseEntity duplicatedStationTitleExceptionResponse() {
        return ResponseEntity.status(409)
            .build();
    }
}
