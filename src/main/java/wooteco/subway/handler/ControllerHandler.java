package wooteco.subway.handler;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.SubwayLineControlException;
import wooteco.subway.exception.dto.ApiError;

@ControllerAdvice
public class ControllerHandler {

    @ExceptionHandler(SubwayLineControlException.class)
    public ResponseEntity<ApiError> invalidSectionForAddition(SubwayLineControlException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.ok(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
        MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
