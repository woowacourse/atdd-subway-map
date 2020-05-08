package wooteco.subway.admin.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.controller.advice.dto.MethodArgumentExceptionDto;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<MethodArgumentExceptionDto>> invalidMethodArguments(MethodArgumentNotValidException e) {
        List<MethodArgumentExceptionDto> exceptionDtos = e.getBindingResult().getAllErrors().stream()
                .map(MethodArgumentExceptionDto::of)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(exceptionDtos);
    }
}
