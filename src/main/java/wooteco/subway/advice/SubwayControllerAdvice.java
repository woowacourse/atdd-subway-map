package wooteco.subway.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.line.repository.LineRepositoryException;
import wooteco.subway.station.repository.StationRepositoryException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class SubwayControllerAdvice {
    @ExceptionHandler({LineRepositoryException.class, StationRepositoryException.class})
    public ResponseEntity<Map<String, String>> handleException(final Exception e) {
        Map<String, String> body = new HashMap<>();
        body.put("Error message", e.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}
