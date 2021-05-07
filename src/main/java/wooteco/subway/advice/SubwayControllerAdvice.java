package wooteco.subway.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.line.repository.DuplicateLineNameException;
import wooteco.subway.line.repository.NoSuchLineException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class SubwayControllerAdvice {
    @ExceptionHandler({DuplicateLineNameException.class, NoSuchLineException.class})
    public ResponseEntity<Map<String, String>> handleException(final Exception e) {
        Map<String, String> body = new HashMap<>();
        body.put("Error message", e.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}
