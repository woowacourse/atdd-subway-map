package wooteco.subway.ui;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.LineNotFoundException;

@RestControllerAdvice(annotations = RestController.class)
public class StationControllerAdvice {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity handleIllegalException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .build();
    }

    @ExceptionHandler(value = LineNotFoundException.class)
    public ResponseEntity handleNotFoundException(EmptyResultDataAccessException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .build();
    }

    @ExceptionHandler(value = LineDuplicateException.class)
    public ResponseEntity handleIllegalException(DuplicateKeyException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .build();
    }
}
