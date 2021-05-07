package wooteco.subway.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.common.exception.SubwayHttpException;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(SubwayHttpException.class)
    public ResponseEntity<String> handler(SubwayHttpException e) {
        return ResponseEntity
                .status(e.httpStatus())
                .body(e.body());
    }
}
