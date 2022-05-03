package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import wooteco.subway.dto.SubwayErrorResponse;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<SubwayErrorResponse> handleBusinessException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(SubwayErrorResponse.from(exception));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Void> handleNoHandlerFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Void> handleException() {
        return ResponseEntity.internalServerError().build();
    }
}
