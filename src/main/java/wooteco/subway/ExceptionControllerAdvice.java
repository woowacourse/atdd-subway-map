package wooteco.subway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.line.exception.LineIllegalArgumentException;
import wooteco.subway.station.exception.StationIllegalArgumentException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> unpredictableException(Exception error) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException error) {
        return ResponseEntity.badRequest().body(error.getMessage());
    }

    @ExceptionHandler(StationIllegalArgumentException.class)
    public ResponseEntity<String> StationIllegalArgumentExceptionHandler(StationIllegalArgumentException error) {
        return ResponseEntity.badRequest().body(error.getMessage());
    }

    @ExceptionHandler(LineIllegalArgumentException.class)
    public ResponseEntity<String> LineIllegalArgumentExceptionHandler(LineIllegalArgumentException error) {
        return ResponseEntity.badRequest().body(error.getMessage());
    }
}
