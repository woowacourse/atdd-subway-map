package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.exception.ClientException;

@RestControllerAdvice
public class StationControllerAdvice {

    @ExceptionHandler({ClientException.class})
    public ResponseEntity<String> IllegalArgumentExceptionHandle(ClientException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
