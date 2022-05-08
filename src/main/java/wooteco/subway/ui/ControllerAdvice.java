package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ErrorMessageResponse;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageResponse> illegalArgumentExceptionHandler(RuntimeException e) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorMessageResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> exceptionHandler(Exception e) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse("서버 에러가 발생했습니다.");
        return ResponseEntity.internalServerError().body(errorMessageResponse);
    }
}
