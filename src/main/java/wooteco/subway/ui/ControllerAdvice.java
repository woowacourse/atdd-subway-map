package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.response.ErrorMessageResponse;

@RestControllerAdvice
public class ControllerAdvice {

    private static final String INTERNAL_EXCEPTION_MESSAGE = "서버 내부 에러입니다.";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageResponse> handleIllegalArgumentException(RuntimeException e) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorMessageResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleException() {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(INTERNAL_EXCEPTION_MESSAGE);
        return ResponseEntity.internalServerError().body(errorMessageResponse);
    }
}
