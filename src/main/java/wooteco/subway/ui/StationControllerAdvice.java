package wooteco.subway.ui;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.NoLineFoundException;
import wooteco.subway.exception.NoStationFoundException;
import wooteco.subway.exception.StationDuplicateException;

@RestControllerAdvice
public class StationControllerAdvice {

    @ExceptionHandler({StationDuplicateException.class, LineDuplicateException.class, NoLineFoundException.class,
            NoStationFoundException.class, DuplicateKeyException.class})
    public ResponseEntity<Void> duplicateStation() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
