package wooteco.subway.admin.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.controller.advice.dto.ExceptionDto;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDto> getIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ExceptionDto(e.getMessage()));
    }
}