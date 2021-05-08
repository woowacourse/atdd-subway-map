package wooteco.subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.station.exception.StationException;

@ControllerAdvice
public final class GlobalAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleUnknownException(final RuntimeException e) {
        final String message = "unhandled exceptions";
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(StationException.class)
    public ResponseEntity<String> handleStationException(final StationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(LineException.class)
    public ResponseEntity<String> handleLineException(final LineException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
