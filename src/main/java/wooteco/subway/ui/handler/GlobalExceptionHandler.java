package wooteco.subway.ui.handler;

import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateDataException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<String> handleDuplicateKey(DuplicateDataException e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Void> handleDataNotFound() {
        return ResponseEntity.notFound()
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleInvalidArguments(MethodArgumentNotValidException e) {
        String exceptionMessage = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(System.lineSeparator()));
        return ResponseEntity.badRequest().body(exceptionMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedException() {
        return ResponseEntity.internalServerError()
                .body("서버에서 예상하지 못한 문제가 발생했습니다.");
    }

}
