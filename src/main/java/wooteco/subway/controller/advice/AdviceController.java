package wooteco.subway.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.ErrorMessage;
import wooteco.subway.exception.HttpException;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ErrorMessage> handleException(HttpException e) {
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(e.getErrorMessage());
    }
}
