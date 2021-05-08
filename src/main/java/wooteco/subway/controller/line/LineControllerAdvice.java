package wooteco.subway.controller.line;

import java.sql.SQLNonTransientException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.NotFoundLineException;

@RestControllerAdvice
public class LineControllerAdvice {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Void> duplicateLineAccessExceptionResponse(final SQLNonTransientException e) {
        return ResponseEntity.badRequest()
            .build();
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity<Void> voidLineDeleteExceptionResponse(final NotFoundLineException e) {
        return ResponseEntity.badRequest()
            .build();
    }
}
