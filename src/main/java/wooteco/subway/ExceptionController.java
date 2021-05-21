package wooteco.subway;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.section.DuplicateStationException;
import wooteco.subway.exception.section.InvalidDistanceException;
import wooteco.subway.exception.section.InvalidSectionOnLineException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.exception.station.NotFoundStationException;
import wooteco.subway.exception.line.NullColorException;
import wooteco.subway.exception.NullException;
import wooteco.subway.exception.NullIdException;
import wooteco.subway.exception.NullNameException;

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
    public ResponseEntity<Void> voidLineDeleteExceptionResponse(
        final EmptyResultDataAccessException e) {
        return ResponseEntity.notFound()
            .build();
    }

    @ExceptionHandler(InvalidDistanceException.class)
    public ResponseEntity<Void> invalidDistanceExceptionResponse(final InvalidDistanceException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler({NullIdException.class, NullNameException.class, NullColorException.class})
    public ResponseEntity<Void> nullExceptionResponse(final NullException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(DuplicateStationException.class)
    public ResponseEntity<Void> duplicatedStationExceptionResponse(
        final DuplicateStationException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> methodArgumentNotValidExceptionResponse(
        final MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(InvalidSectionOnLineException.class)
    public ResponseEntity<Void> alreadyExistedStationsOnLineExceptionResponse(
        final InvalidSectionOnLineException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> illegalArgumentExceptionResponse(final IllegalArgumentException e) {
        return ResponseEntity.notFound()
            .build();
    }
}

