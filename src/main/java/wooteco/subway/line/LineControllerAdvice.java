package wooteco.subway.line;

import java.sql.SQLNonTransientException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.VoidLineException;

@RestControllerAdvice
public class LineControllerAdvice {

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity voidLineAccessExceptionResponse(EmptyResultDataAccessException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity duplicateLineAccessExceptionResponse(SQLNonTransientException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(VoidLineException.class)
    public ResponseEntity voidLineDeleteExceptionResponse(VoidLineException e) {
        return ResponseEntity.noContent().build();
    }
}
