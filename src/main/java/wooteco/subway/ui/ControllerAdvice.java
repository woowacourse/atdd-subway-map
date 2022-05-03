package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ErrorMessageResponse;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageResponse> exceptionHandler(RuntimeException e) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorMessageResponse);
    }
}
