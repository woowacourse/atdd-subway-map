package wooteco.subway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.exception.illegalexception.IllegalException;
import wooteco.subway.exception.notfoundexception.NotFoundException;
import wooteco.subway.exception.sectionsexception.SectionsException;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<String> subWayException(SubwayException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DuplicatedNameException.class)
    public ResponseEntity<String> duplicatedException(DuplicatedNameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> notFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalException.class)
    public ResponseEntity<String> illegalException(IllegalException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(SectionsException.class)
    public ResponseEntity<String> securityException(SectionsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
