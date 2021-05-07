package wooteco.subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.line.LineException;
import wooteco.subway.station.StationException;

@ControllerAdvice
public class GlobalAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleUnknownException(final RuntimeException e) {
        final String message = "unhandled exceptions";
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler({StationException.class, LineException.class})
    public ResponseEntity<String> handleStationException(final RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
