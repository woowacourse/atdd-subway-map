package wooteco.subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.station.StationException;

@ControllerAdvice
final public class GlobalAdvice {

    @ExceptionHandler(RuntimeException.class)
    final public ResponseEntity<String> handleUnknownException(final RuntimeException e) {
        final String message = "unhandled exceptions";
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(StationException.class)
    final public ResponseEntity<String> handleStationException(final StationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
