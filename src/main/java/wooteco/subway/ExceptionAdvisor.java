package wooteco.subway;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.exception.line.SubwayLineException;
import wooteco.subway.exception.station.SubwayStationException;

@RestControllerAdvice
public class ExceptionAdvisor {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicationKeyException(Exception e) {
        return ResponseEntity.badRequest().body("중복 등록을할 수 없습니다");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDatabaseExceptions(Exception e) {
        return ResponseEntity.badRequest().body("데이터베이스 에러");
    }

    @ExceptionHandler({SubwayStationException.class, SubwayLineException.class})
    public ResponseEntity<String> handleSubwayExceptions(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

}
