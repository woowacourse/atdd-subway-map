package wooteco.subway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.SubwayException;

@RestControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler({SubwayException.class})
    public ResponseEntity<String> error(SubwayException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Void> error() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
