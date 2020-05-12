package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<String> requestHandle(MethodArgumentNotValidException e) {
        StringBuilder stringBuilder = new StringBuilder();
        BindingResult bindingResult = e.getBindingResult();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append(fieldError.getDefaultMessage())
                    .append("\n");
        }
        return ResponseEntity.badRequest()
                .body(stringBuilder.toString());
    }


    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> runtimeHandle(RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

}
