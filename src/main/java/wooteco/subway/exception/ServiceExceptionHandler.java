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

    @ExceptionHandler(AccessNoneDataException.class)
    public ResponseEntity<ErrorMessageResponse> handleAccessNoneDataException(AccessNoneDataException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(e.getMessage()));
    }

    @ExceptionHandler(SectionServiceException.class)
    public ResponseEntity<ErrorMessageResponse> handleSectionServiceException(SectionServiceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(e.getMessage()));
    }
}
