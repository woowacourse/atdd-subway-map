package wooteco.subway.admin.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.controller.advice.dto.MethodArgumentExceptionDto;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ValidationExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<MethodArgumentExceptionDto>> invalidMethodArguments(MethodArgumentNotValidException e) {
        List<MethodArgumentExceptionDto> exceptionDtos = new ArrayList<>();

        e.getBindingResult().getAllErrors()
                .forEach(error -> exceptionDtos.add(MethodArgumentExceptionDto.of(error)));

        return ResponseEntity.badRequest().body(exceptionDtos);
    }
}
