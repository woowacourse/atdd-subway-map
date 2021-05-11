package wooteco.subway.exception;

import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> emptyResultDataAccessExceptionHandle(SQLException e) {
        ErrorResponse response = ErrorResponse.of("Database Error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SectionException.class)
    public ResponseEntity<ErrorResponse> uniqueSectionDeleteExceptionHandle(RuntimeException e) {
        ErrorResponse response = ErrorResponse.of("Section Error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
