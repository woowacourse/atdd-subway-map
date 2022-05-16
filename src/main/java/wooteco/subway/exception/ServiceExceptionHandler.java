package wooteco.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorMessageResponse;

@ControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler(DataLengthException.class)
    public ResponseEntity<ErrorMessageResponse> handleDataLengthException(DataLengthException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleAccessNoneDataException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageResponse> handleSectionServiceException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(e.getMessage()));
    }

    @ExceptionHandler(DeleteUsingDateException.class)
    public ResponseEntity<ErrorMessageResponse> handleDeleteUsingDateException(DeleteUsingDateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(e.getMessage()));
    }
}
