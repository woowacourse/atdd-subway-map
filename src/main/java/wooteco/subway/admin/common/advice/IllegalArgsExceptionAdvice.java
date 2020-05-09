package wooteco.subway.admin.common.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.common.advice.dto.UnprocessableExceptionDto;

@RestControllerAdvice
public class IllegalArgsExceptionAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<UnprocessableExceptionDto> illegalArgsExceptionHandle(IllegalArgumentException e) {
        return ResponseEntity.unprocessableEntity().body(new UnprocessableExceptionDto(e.getMessage()));
    }
}
