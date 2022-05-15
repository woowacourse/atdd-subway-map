package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ErrorMessageResponse;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorMessageResponse> illegalArgumentExceptionHandler(final RuntimeException e) {
        final ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorMessageResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> exceptionHandler(final Exception e) {
        final ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse("서버 에러가 발생했습니다.");
        return ResponseEntity.internalServerError().body(errorMessageResponse);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorMessageResponse> validExceptionHandler(final MethodArgumentNotValidException ex) {
        final ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.badRequest().body(errorMessageResponse);
    }
}
