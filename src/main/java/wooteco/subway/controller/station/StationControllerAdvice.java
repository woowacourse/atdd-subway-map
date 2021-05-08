package wooteco.subway.controller.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.NotFoundStationException;

@RestControllerAdvice
public class StationControllerAdvice {

    @ExceptionHandler(NotFoundStationException.class)
    public ResponseEntity<Void> voidStationExceptionResponse() {
        return ResponseEntity.badRequest()
            .build();
    }

}
