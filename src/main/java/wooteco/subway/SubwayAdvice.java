package wooteco.subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.SubwayException;

@RestControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler({SubwayException.class})
    public ResponseEntity<String> error(SubwayException e) {
        return new ResponseEntity(e.getMessage(), e.getHttpStatus());
    }
}
