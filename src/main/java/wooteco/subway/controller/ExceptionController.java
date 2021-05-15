package wooteco.subway.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.InvalidDistanceException;
import wooteco.subway.exception.InvalidSectionOnLineException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.exception.NullColorException;
import wooteco.subway.exception.NullException;
import wooteco.subway.exception.NullIdException;
import wooteco.subway.exception.NullNameException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Void> duplicateExceptionResponse(DuplicateKeyException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> notFoundExceptionResponse(NotFoundException e) {
        return ResponseEntity.notFound()
            .build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Void> voidLineDeleteExceptionResponse(
        EmptyResultDataAccessException e) {
        return ResponseEntity.notFound()
            .build();
    }

    @ExceptionHandler(InvalidDistanceException.class)
    public ResponseEntity<Void> invalidDistanceExceptionResponse(InvalidDistanceException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler({NullIdException.class, NullNameException.class, NullColorException.class})
    public ResponseEntity<Void> nullExceptionResponse(NullException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Void> duplicatedStationExceptionResponse(DuplicateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> methodArgumentNotValidExceptionResponse(
        MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(InvalidSectionOnLineException.class)
    public ResponseEntity<Void> alreadyExistedStationsOnLineExceptionResponse(
        InvalidSectionOnLineException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> illegalArgumentExceptionResponse(IllegalArgumentException e) {
        return ResponseEntity.notFound()
            .build();
    }
}
