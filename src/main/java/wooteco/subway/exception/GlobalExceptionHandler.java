package wooteco.subway.exception;

import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> DataAccessExceptionHandle(SQLException e) {
        ErrorResponse response = ErrorResponse.of("Database Error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {InvalidSectionDistanceException.class,
        NoneOrAllStationsExistingInLineException.class, SameStationIdException.class,
        UniqueSectionDeleteException.class})
    public ResponseEntity<ErrorResponse> SectionExceptionHandle(SectionException e) {
        ErrorResponse response = ErrorResponse.of("Section Error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidStationException.class)
    public ResponseEntity<ErrorResponse> invalidStationExceptionHandle(RuntimeException e) {
        ErrorResponse response = ErrorResponse.of("Station Error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
