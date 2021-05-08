package wooteco.subway.controller;

import java.sql.SQLNonTransientException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.exception.station.NotFoundStationException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Void> duplicateExceptionResponse(final DuplicateKeyException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .build();
    }

    @ExceptionHandler({NotFoundLineException.class, NotFoundStationException.class})
    public ResponseEntity<Void> notFoundExceptionResponse(final NotFoundException e) {
        return ResponseEntity.notFound()
            .build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Void> voidLineDeleteExceptionResponse(final EmptyResultDataAccessException e) {
        return ResponseEntity.notFound()
            .build();
    }
}
