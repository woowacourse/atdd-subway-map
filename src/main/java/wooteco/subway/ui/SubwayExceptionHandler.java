package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ExceptionResponse;
import wooteco.subway.exception.InvalidInputException;

@RestControllerAdvice
public class SubwayExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ExceptionResponse> handleUserException(InvalidInputException invalidInputException) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(invalidInputException.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalStateException(IllegalStateException illegalStateException) {
        return ResponseEntity.internalServerError().body(new ExceptionResponse(illegalStateException.getMessage()));
    }
}
