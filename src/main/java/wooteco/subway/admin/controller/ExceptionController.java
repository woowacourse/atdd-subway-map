package wooteco.subway.admin.controller;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.ErrorResponse;

@ControllerAdvice
@RestController
public class ExceptionController {

    @ExceptionHandler(value = {DbActionExecutionException.class})
    public ResponseEntity handleDbActionExecutionException() {
        return new ResponseEntity(new ErrorResponse("DUPLICATED"),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
