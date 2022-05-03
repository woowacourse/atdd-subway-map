package wooteco.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorMessageResponse;
import wooteco.subway.exception.DuplicateStationNameException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(DuplicateStationNameException.class)
    public ResponseEntity<ErrorMessageResponse> duplicateStationNameException(DuplicateStationNameException duplicateStationNameException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(duplicateStationNameException.getMessage()));
    }
}
