package wooteco.subway.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.common.exception.bad_request.BadRequestException;
import wooteco.subway.common.exception.not_found.NotFoundException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Void> badRequestExceptionHandler() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> notFoundExceptionHandler() {
        return ResponseEntity.notFound().build();
    }
}
