package wooteco.subway.ui;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.IllegalSectionCreatedException;
import wooteco.subway.exception.IllegalSectionDeleteException;
import wooteco.subway.exception.NameDuplicationException;

@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Void> exception() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Void> queryObjectExceptions() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalSectionCreatedException.class)
    public ResponseEntity<String> impossibleSection(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalSectionDeleteException.class)
    public ResponseEntity<String> deleteSectionException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NameDuplicationException.class)
    public ResponseEntity<String> nameDuplicationException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
