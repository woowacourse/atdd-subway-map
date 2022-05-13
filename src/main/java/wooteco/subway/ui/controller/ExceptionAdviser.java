package wooteco.subway.ui.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.dto.response.ExceptionResponse;

@RestControllerAdvice
public class ExceptionAdviser {

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<ExceptionResponse> handleRequestError(Exception exception) {
        return ResponseEntity.badRequest().body(ExceptionResponse.of(exception));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionResponse> handleServerError(Exception exception) {
        return ResponseEntity.internalServerError().body(ExceptionResponse.of(exception));
    }
}
