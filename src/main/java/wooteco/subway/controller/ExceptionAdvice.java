package wooteco.subway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.SubwayException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionAdvice {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<Object> handleSubwayException(SubwayException e) {
        logger.error(String.valueOf(e.getBody()));
        return ResponseEntity.status(e.getHttpStatus()).body(e.getBody());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach(error -> {
                    errors.put(((FieldError) error).getField(), error.getDefaultMessage());
                    logger.error("%s : %s", ((FieldError) error).getField(), error.getDefaultMessage());
                });
        return ResponseEntity.badRequest().body(errors);
    }
}
