package wooteco.subway.admin.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.ErrorResponse;
import wooteco.subway.admin.exception.DuplicateLineStationException;

@ControllerAdvice
@RestController
public class ExceptionController {
    @ExceptionHandler(value = {DuplicateKeyException.class, DuplicateLineStationException.class})
    public ResponseEntity<ErrorResponse> handleDbActionExecutionException() {
        return new ResponseEntity<>(new ErrorResponse("DUPLICATED"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
