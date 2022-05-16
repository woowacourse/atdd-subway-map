package wooteco.subway.ui.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import wooteco.subway.exception.duplicate.DuplicateDataException;
import wooteco.subway.exception.notfound.DataNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DuplicateDataException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleDuplicateKey(Exception e) {
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

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, InvalidFormatException.class})
    public ResponseEntity<String> handleInvalidType() {
        return ResponseEntity.badRequest().body("요청하는 타입이 올바르지 않습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError()
                .body("서버에서 예상하지 못한 문제가 발생했습니다.");
    }

}
