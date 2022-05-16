package wooteco.subway.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.utils.exception.SubwayException;

@RestControllerAdvice
public class ControllerAdvice {

    private final Logger logger;

    public ControllerAdvice() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ErrorResponse> subwayExceptionHandler(SubwayException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> nullPointerExceptionHandler(NullPointerException e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> ExceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(new ErrorResponse("[ERROR] 예상치 못한 에러가 발생했습니다."));
    }
}
