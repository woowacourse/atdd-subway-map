package wooteco.subway.admin.common.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.common.response.DefaultResponse;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultResponse<String>> invalidMethodArguments(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(System.lineSeparator()));
        return ResponseEntity.badRequest().body(DefaultResponse.error(message));
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<DefaultResponse<String>> invalidJsonInput(InvalidFormatException e) {
        return ResponseEntity.badRequest().body(DefaultResponse.error(
                String.format("%s가 올바르지 않은 값(%s)입니다.", e.getPath().get(0).getFieldName(), e.getValue())));
    }
}
