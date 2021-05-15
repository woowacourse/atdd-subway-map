package wooteco.subway.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {StationController.class})
public class StationControllerAdvice {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicateUniqueColumnException(DuplicateKeyException exception) {
        return ResponseEntity.badRequest()
                .body("이미 존재하는 역 이름입니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleInternalServerException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
}
