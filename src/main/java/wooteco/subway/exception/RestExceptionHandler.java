package wooteco.subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ErrorDto;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorDto> handle(RuntimeException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorDto> handleException(Exception exception) {
        exception.printStackTrace();
        return ResponseEntity.internalServerError().body(new ErrorDto("서버 내에 오류가 발생했습니다."));
    }
}
