package wooteco.subway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.DuplicatedNameException;

@RestControllerAdvice
public class ExceptionAdvice {
    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(DuplicatedNameException.class)
    public ResponseEntity<String> handleIllegalArgumentException(DuplicatedNameException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Void> databaseErrorLog(DataAccessException e) {
        log.error("Data Access Error!" + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
