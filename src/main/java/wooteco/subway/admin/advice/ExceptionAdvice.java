package wooteco.subway.admin.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.exception.CommonException;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(CommonException.class)
    public ResponseEntity handleCommonException(CommonException commonException) {
        return new ResponseEntity<>(commonException.getMessage(), commonException.getHttpStatus());
    }
}
