package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ExceptionResponse;
import wooteco.subway.exception.InvalidInputException;

@RestControllerAdvice
public class SubwayExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ExceptionResponse> handleException(InvalidInputException invalidInputException) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(invalidInputException.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException2(IllegalArgumentException illegalArgumentException) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(illegalArgumentException.getMessage()));
    }
}
