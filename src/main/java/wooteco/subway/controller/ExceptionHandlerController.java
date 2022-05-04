package wooteco.subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorMessageResponse;
import wooteco.subway.exception.DuplicateNameException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<ErrorMessageResponse> duplicateStationNameException(DuplicateNameException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(e.getMessage()));
    }
}
