package wooteco.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.response.ErrorResponse;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<ErrorResponse> duplicateStation(final IllegalStateException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> unexpectedError() {
        return new ResponseEntity<>(new ErrorResponse("실행할 수 없는 명령입니다."), HttpStatus.BAD_REQUEST);
    }
}
