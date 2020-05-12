package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.dto.res.ErrorResponse;
import wooteco.subway.admin.exceptions.DuplicateLineNameException;
import wooteco.subway.admin.exceptions.DuplicateLineStationException;
import wooteco.subway.admin.exceptions.LineNotFoundException;
import wooteco.subway.admin.exceptions.StationNotFoundException;

@RestControllerAdvice
public class SubwayAdvice {
    @ExceptionHandler(DuplicateLineNameException.class)
    public ResponseEntity<ErrorResponse> duplicateLineNameHandler(DuplicateLineNameException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<ErrorResponse> LineNotFoundHandler(LineNotFoundException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<ErrorResponse> StationNotFoundHandler(StationNotFoundException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(DuplicateLineStationException.class)
    public ResponseEntity<ErrorResponse> DuplicateLineStationHandler(DuplicateLineStationException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
    }
}
