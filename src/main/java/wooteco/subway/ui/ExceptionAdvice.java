package wooteco.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.exception.InternalServerException;
import wooteco.subway.exception.NotFoundException;

@ControllerAdvice(assignableTypes = {StationController.class, LineController.class})
public class ExceptionAdvice {

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InternalServerException.class})
    public ResponseEntity<ErrorResponse> handleInternalServerException(Exception exception) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(exception.getMessage()));
    }
}
