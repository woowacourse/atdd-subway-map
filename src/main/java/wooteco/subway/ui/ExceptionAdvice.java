package wooteco.subway.ui;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.IllegalSectionCreatedException;
import wooteco.subway.exception.NameDuplicationException;

@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(NameDuplicationException.class)
    public ResponseEntity<Void> duplicatedNameFound() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Void> queryObjectExceptions() {
        return ResponseEntity.badRequest().build();
    }

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<Void> exceptions() {
//        return ResponseEntity.badRequest().build();
//    }

    @ExceptionHandler(IllegalSectionCreatedException.class)
    public ResponseEntity<Void> impossibleSection() {
        return ResponseEntity.badRequest().build();
    }
}
