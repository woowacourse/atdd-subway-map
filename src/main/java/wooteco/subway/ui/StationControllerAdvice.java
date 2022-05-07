package wooteco.subway.ui;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.GlobalUnknownException;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.exception.StationDuplicateException;
import wooteco.subway.ui.dto.ExceptionResponse;

@RestControllerAdvice(basePackageClasses = {LineController.class, StationController.class})
public class StationControllerAdvice {

    @ExceptionHandler({StationDuplicateException.class, LineDuplicateException.class, NotFoundLineException.class,
            NotFoundStationException.class, DuplicateKeyException.class})
    public ResponseEntity<ExceptionResponse> duplicateStation(Exception e) {
        return new ResponseEntity<>(ExceptionResponse.from(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalUnknownException> unknownException(Exception e) {
        return new ResponseEntity<>(new GlobalUnknownException(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
