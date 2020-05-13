package wooteco.subway.admin.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object invalidMethodArguments(MethodArgumentNotValidException e) {
        return e.getMessage();
    }
}
