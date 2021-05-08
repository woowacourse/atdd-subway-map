package wooteco.subway.controller;

import java.sql.SQLNonTransientException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.exception.station.NotFoundStationException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Void> duplicateLineAccessExceptionResponse(final SQLNonTransientException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .build();
    }

    @ExceptionHandler({NotFoundLineException.class, NotFoundStationException.class})
    public ResponseEntity<Void> voidLineDeleteExceptionResponse(final NotFoundLineException e) {
        return ResponseEntity.badRequest()
            .build();
    }
}
