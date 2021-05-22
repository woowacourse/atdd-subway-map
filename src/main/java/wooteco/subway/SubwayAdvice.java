package wooteco.subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.IllegalUserInputException;
import wooteco.subway.exception.InvalidInputDataException;
import wooteco.subway.exception.NotExistItemException;
import wooteco.subway.exception.UseForeignKeyException;

@ControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<String> handleDuplicateException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotExistItemException.class)
    public ResponseEntity<String> handleNotExistItemException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidInputDataException.class)
    public ResponseEntity<String> handleNotInputDataException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalUserInputException.class)
    public ResponseEntity<String> handleIllegalUserInputException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(UseForeignKeyException.class)
    public ResponseEntity<String> handleUseForeignKeyException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        System.out.println(e.getMessage());
        return ResponseEntity.badRequest().body("알 수 없는 에러가 발상했습니다.");
    }
}
