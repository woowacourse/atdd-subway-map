package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ExceptionResponse;
import wooteco.subway.exception.CustomException;

@RestControllerAdvice
public class SubwayExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleException(CustomException customException) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(customException.getMessage()));
    }
}
