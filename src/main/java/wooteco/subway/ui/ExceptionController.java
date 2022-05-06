package wooteco.subway.ui;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({IllegalArgumentException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<String> handelIllegalArgument(Exception e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException() {
        return ResponseEntity.internalServerError()
                .body("예기치 못한 문제가 발생했습니다.");
    }

}
