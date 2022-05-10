package wooteco.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.exception.DataNotFoundException;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("서버 오류입니다."));
    }
}
