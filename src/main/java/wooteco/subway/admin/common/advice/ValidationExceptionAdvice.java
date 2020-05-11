package wooteco.subway.admin.common.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.common.advice.dto.MethodArgumentExceptionDto;

import java.util.Collections;
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

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<List<MethodArgumentExceptionDto>> invalidJsonInput(InvalidFormatException e) {
        return ResponseEntity.badRequest().body(Collections.singletonList(new MethodArgumentExceptionDto(e.getPath().get(0).getFieldName(),
                String.format("%s가 올바르지 않은 값(%s)입니다.", e.getPath().get(0).getFieldName(), e.getValue()))));
    }
}
