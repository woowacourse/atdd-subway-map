package wooteco.subway.ui;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException exception) {
        Map<String, String> body = new HashMap<>();
        body.put("message", exception.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({DuplicateKeyException.class})
    public ResponseEntity<Map<String, String>> handleSqlException(DuplicateKeyException exception) {
        logger.error(exception.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("message", "이미 존재하는 데이터 입니다.");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<Map<String, String>> handleEmptyResultException(EmptyResultDataAccessException exception) {
        logger.error(exception.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("message", "데이터가 존재하지 않습니다.");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map<String, String>> handleException(Exception exception) {
        logger.error(exception.getMessage());
        return ResponseEntity.internalServerError().build();
    }
}
