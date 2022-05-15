package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.SubwayException;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<String> subwayException(final SubwayException exception) {
        exception.printStackTrace();
        return ResponseEntity.status(exception.getHttpStatus()).body(exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> unexpectedError(final Exception exception) {
        exception.printStackTrace();
        return ResponseEntity.internalServerError().body("[ERROR] 예기치 못한 에러가 발생했습니다.");
    }
}
