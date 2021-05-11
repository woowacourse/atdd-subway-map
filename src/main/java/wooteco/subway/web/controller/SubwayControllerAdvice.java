package wooteco.subway.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.web.exception.SubwayException;
import wooteco.subway.web.exception.SubwayHttpException;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(SubwayHttpException.class)
    public ResponseEntity<String> handler(SubwayHttpException e) {
        return ResponseEntity
                .status(e.httpStatus())
                .body(e.body());
    }

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<String> handler(SubwayException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handler(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getStackTrace().toString());
    }
}
