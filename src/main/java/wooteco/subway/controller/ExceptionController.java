package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.SubwayException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<String> handleSubwayException(SubwayException subwayException) {
        int statusCode = subwayException.getStatusCode();
        String message = subwayException.getMessage();
        return ResponseEntity.status(statusCode)
                .body(message);
    }
}
