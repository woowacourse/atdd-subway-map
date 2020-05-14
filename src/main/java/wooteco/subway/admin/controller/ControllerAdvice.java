package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.exception.*;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<String> requestHandle(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body("잘못된 요청입니다.");
    }

    @ExceptionHandler({ NotFoundLineException.class, NotFoundLineStationException.class, NotFoundStationException.class,
            DuplicatedLineException.class, DuplicatedLineStationException.class, DuplicatedStationException.class,
            InvalidStationNameException.class })
    public ResponseEntity<String> runtimeHandle(RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}
