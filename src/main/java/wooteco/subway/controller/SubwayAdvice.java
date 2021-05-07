package wooteco.subway.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.SubwayException;

@ControllerAdvice
public class SubwayAdvice {
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleSQLException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL 에러 발생");
    }

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<String> handleSubwayException(SubwayException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }
}
