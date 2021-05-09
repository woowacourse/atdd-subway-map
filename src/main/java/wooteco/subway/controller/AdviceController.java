package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.SubwayException;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity handleException(SubwayException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
