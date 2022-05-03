package wooteco.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.StationDuplicateException;

@RestControllerAdvice
public class StationControllerAdvice {

    @ExceptionHandler(StationDuplicateException.class)
    public ResponseEntity<Void> duplicateStation() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
