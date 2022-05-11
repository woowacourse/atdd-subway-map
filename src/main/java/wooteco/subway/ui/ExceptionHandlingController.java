package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.BothUpAndDownStationDoNotExistException;
import wooteco.subway.exception.BothUpAndDownStationExistException;
import wooteco.subway.exception.CanNotInsertSectionException;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.exception.OnlyOneSectionException;

@RestControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler({NotFoundLineException.class, NotFoundStationException.class})
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({DuplicateNameException.class, CanNotInsertSectionException.class, OnlyOneSectionException.class,
            BothUpAndDownStationExistException.class,
            BothUpAndDownStationDoNotExistException.class})
    public ResponseEntity<String> handleDuplicateName(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
