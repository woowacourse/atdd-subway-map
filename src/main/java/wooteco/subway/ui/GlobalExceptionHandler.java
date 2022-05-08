package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.application.exception.DuplicateException;
import wooteco.subway.application.exception.NotFoundException;
import wooteco.subway.application.exception.UnaddableSectionException;
import wooteco.subway.application.exception.UndeletableSectionException;
import wooteco.subway.domain.exception.BlankArgumentException;
import wooteco.subway.domain.exception.UnmergeableException;
import wooteco.subway.domain.exception.UnsplittableException;
import wooteco.subway.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DuplicateException.class, BlankArgumentException.class,
        UnaddableSectionException.class, UndeletableSectionException.class,
        UnsplittableException.class, UnmergeableException.class})
    private ResponseEntity<ErrorResponse> handleExceptionToBadRequest(Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<Void> handleExceptionToNotFound() {
        return ResponseEntity.notFound().build();
    }
}
