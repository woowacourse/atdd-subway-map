package wooteco.subway.exception.advice;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.EntityNotFoundException;
import wooteco.subway.exception.InvalidRequestException;
import wooteco.subway.exception.response.ErrorResponse;

@RestControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(value = DuplicateNameException.class)
    public ResponseEntity<ErrorResponse> duplicateNameExceptionHandler(DuplicateNameException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> entityNotFoundExceptionHandler(EntityNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionExceptionHandler(BindingResult bindingResult) {
        final String message = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }

    @ExceptionHandler(value = InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> invalidRequestExceptionHandler(InvalidRequestException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentExceptionHandler() {
        return ResponseEntity.status(500).body(new ErrorResponse("예상치 못한 서버 에러입니다 ㅠㅡㅜ"));
    }
}
