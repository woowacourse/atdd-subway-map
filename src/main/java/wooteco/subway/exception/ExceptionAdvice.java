package wooteco.subway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(DuplicatedNameException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(DuplicatedNameException e) {
        log.error(e.getMessage());
        Class<? extends DuplicatedNameException> causedClass = e.getClass();
        ExceptionResponse exceptionResponse = new ExceptionResponse(causedClass.getName(), e.getMessage());
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionResponse> databaseErrorLog(DataAccessException e) {
        log.error(e.getMessage());
        Class<? extends DataAccessException> causedClass = e.getClass();
        ExceptionResponse exceptionResponse = new ExceptionResponse(causedClass.getName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }
}
