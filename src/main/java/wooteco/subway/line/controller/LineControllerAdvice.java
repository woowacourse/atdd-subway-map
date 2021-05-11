package wooteco.subway.line.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.exception.line.NotFoundLineException;

@RestControllerAdvice
public class LineControllerAdvice {

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity voidLineAccessExceptionResponse(final EmptyResultDataAccessException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(DuplicateLineException.class)
    public ResponseEntity duplicateLineAccessExceptionResponse(final DuplicateLineException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity voidLineDeleteExceptionResponse(final NotFoundLineException e) {
        return ResponseEntity.badRequest()
            .build();
    }
}
