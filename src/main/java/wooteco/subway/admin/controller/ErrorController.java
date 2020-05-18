package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.dto.ErrorResponse;
import wooteco.subway.admin.exception.StartStationNotFoundException;

import java.sql.SQLException;

@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException e) {
        final ErrorResponse response = ErrorResponse.of("SQL 에러입니다.");
        return new ResponseEntity<>(response, HttpStatus.valueOf(500));
    }

    @ExceptionHandler(StartStationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStartStationException(StartStationNotFoundException e) {
        final ErrorResponse response = ErrorResponse.of("시작역을 찾을 수 없습니다.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(StartStationNotFoundException e) {
        final ErrorResponse response = ErrorResponse.of(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
