package wooteco.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(WebException.class)
    public ResponseEntity<String> handle(WebException e) {
        return new ResponseEntity(e.getBody(), e.getHttpStatus());
    }
}
