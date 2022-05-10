package wooteco.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.service.dto.ErrorResponse;
import wooteco.subway.utils.exception.CannotCreateClassException;
import wooteco.subway.utils.exception.DuplicatedException;
import wooteco.subway.utils.exception.NoTerminalStationException;
import wooteco.subway.utils.exception.NotFoundException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({DuplicatedException.class, NoTerminalStationException.class, CannotCreateClassException.class})
    public ResponseEntity<ErrorResponse> nameDuplicatedExceptionHandler(RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<Void> serverExceptionHandler() {
        return ResponseEntity.internalServerError().build();
    }
}
