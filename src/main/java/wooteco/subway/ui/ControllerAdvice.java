package wooteco.subway.ui;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.domain.exception.ExpectedException;
import wooteco.subway.exception.ClientRuntimeException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ClientRuntimeException.class)
    public ResponseEntity<String> handleClientRuntimeException(final ClientRuntimeException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(ExpectedException.class)
    public ResponseEntity<String> handleExceptedException(final ExpectedException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(extractErrorMessage(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleServerException(final Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("서버에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }

    private String extractErrorMessage(final MethodArgumentNotValidException e) {
        return e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
    }
}
