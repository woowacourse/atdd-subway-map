package wooteco.subway;

import java.sql.SQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.dto.StationResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<LineResponse> exceptionHandler(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<StationResponse> sqlExceptionHandler(SQLException e) {
        return ResponseEntity.badRequest().build();
    }
}
