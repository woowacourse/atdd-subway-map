package wooteco.subway.admin.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.controller.advice.dto.ExceptionDto;
import wooteco.subway.admin.domain.exception.SubwayException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ExceptionDto> handleException(Exception e) {
        return ResponseEntity.badRequest().body(new ExceptionDto(e.getMessage()));
    }
}