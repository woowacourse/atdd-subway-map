package wooteco.subway.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.StationResponse;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler({DuplicateKeyException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<StationResponse> handleBadRequest(Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().build();
    }
}
