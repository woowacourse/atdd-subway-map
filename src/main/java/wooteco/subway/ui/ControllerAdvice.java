package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.ClientRuntimeException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ClientRuntimeException.class)
    public ResponseEntity<String> handleDataNotFoundException(final ClientRuntimeException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleServerException(final Exception e) {
        return ResponseEntity.internalServerError().body("서버에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
