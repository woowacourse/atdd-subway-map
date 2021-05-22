package wooteco.subway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.exception.SubwayCustomException;

@ControllerAdvice
public class SubwayAdvice {

    @ExceptionHandler(SubwayCustomException.class)
    public ResponseEntity<String> handleUseForeignKeyException(SubwayCustomException exception) {
        return ResponseEntity.status(exception.status()).body(exception.message());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        System.out.println(e.getMessage());
        return ResponseEntity.badRequest().body("알 수 없는 에러가 발생했습니다.");
    }
}
