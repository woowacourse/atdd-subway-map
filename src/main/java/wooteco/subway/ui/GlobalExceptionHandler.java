package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.exception.BlankArgumentException;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotDeletableSectionException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.exception.NotSplittableSectionException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DuplicateException.class, BlankArgumentException.class,
        NotSplittableSectionException.class, NotDeletableSectionException.class})
    private ResponseEntity<ErrorResponse> handleExceptionToBadRequest(Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<Void> handleExceptionToNotFound() {
        return ResponseEntity.notFound().build();
    }
}
