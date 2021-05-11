package wooteco.subway;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.repository.DataNotFoundException;
import wooteco.subway.exception.repository.DuplicatedFieldException;
import wooteco.subway.exception.repository.RepositoryException;

@RestControllerAdvice
public class RepositoryExceptionHandler {

    @ExceptionHandler({RepositoryException.class, DataAccessException.class})
    public ResponseEntity<String> handleRepositoryException(final RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> handleDataNotFoundException(final DataNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(DuplicatedFieldException.class)
    public ResponseEntity<String> handleDuplicatedFieldException(final DuplicatedFieldException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
