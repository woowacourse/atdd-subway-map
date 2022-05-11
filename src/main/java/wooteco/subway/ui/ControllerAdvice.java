package wooteco.subway.ui;

import java.util.NoSuchElementException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.response.ErrorMessageResponse;

@RestControllerAdvice
public class ControllerAdvice {

    private static final String INTERNAL_EXCEPTION_MESSAGE = "서버 내부 에러입니다.";

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorMessageResponse> handleNoSuchElementException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorMessageResponse> handleDuplicateKeyException(RuntimeException e) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorMessageResponse);
    }

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
