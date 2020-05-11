package wooteco.subway.admin.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.ErrorResponse;

@ControllerAdvice
@RestController
public class ExceptionController {
    @ExceptionHandler(value = {DuplicateKeyException.class})
    public ResponseEntity handleDbActionExecutionException(DuplicateKeyException e) {
        return new ResponseEntity(new ErrorResponse("DUPLICATED"),
            HttpStatus.BAD_REQUEST);
    }
}
