package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ExceptionResponse;
import wooteco.subway.exception.InvalidSubwayResourceException;

@RestControllerAdvice
public class SubwayExceptionHandler {

    @ExceptionHandler(InvalidSubwayResourceException.class)
    public ResponseEntity<ExceptionResponse> handleException(InvalidSubwayResourceException exception) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(exception.getMessage()));
    }
}
