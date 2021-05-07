package wooteco.subway.controller;

import java.sql.SQLNonTransientException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.NotFoundLineException;

@RestControllerAdvice
public class LineControllerAdvice {

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity voidLineAccessExceptionResponse(final EmptyResultDataAccessException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity duplicateLineAccessExceptionResponse(final SQLNonTransientException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity voidLineDeleteExceptionResponse(final NotFoundLineException e) {
        return ResponseEntity.noContent()
            .build();
    }
}
