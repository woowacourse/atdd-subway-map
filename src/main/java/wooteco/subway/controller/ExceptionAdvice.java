package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.exception.ElementAlreadyExistException;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.IllegalLineColorException;
import wooteco.subway.exception.IllegalLineNameException;
import wooteco.subway.exception.IllegalStationNameException;
import wooteco.subway.exception.NoSuchLineException;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(
            {IllegalLineNameException.class, IllegalLineColorException.class, IllegalStationNameException.class}
    )
    public ResponseEntity<String> illegalInputError(final IllegalInputException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({DuplicateLineNameException.class, DuplicateStationNameException.class})
    public ResponseEntity<String> duplicateNameError(final ElementAlreadyExistException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(NoSuchLineException.class)
    public ResponseEntity<String> noSuchElementError() {
        return ResponseEntity.notFound().build();
    }
}
