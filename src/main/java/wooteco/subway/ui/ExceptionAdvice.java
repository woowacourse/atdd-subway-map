package wooteco.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.exception.DataDuplicationException;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.ui.response.ErrorResponse;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler({IllegalArgumentException.class, DataDuplicationException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotExistException(Exception exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException() {
        return ResponseEntity.internalServerError().body(new ErrorResponse("서버에 문제가 발생했습니다. 다시 시도해주세요."));
    }
}
