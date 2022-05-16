package wooteco.subway.ui;


import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SubwayControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handle(IllegalArgumentException exception) {
        logger.error(exception.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("message", exception.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleSqlException(DuplicateKeyException exception) {
        logger.error(exception.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("message", "이미 존재하는 데이터 입니다.");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidException(
        MethodArgumentNotValidException exception) {
        Map<String, String> body = new HashMap<>();
        String errorMessage = exception.getBindingResult().getAllErrors().get(0)
            .getDefaultMessage();
        logger.error(errorMessage);
        body.put("message", errorMessage);
        return ResponseEntity.badRequest().body(body);
    }
}
