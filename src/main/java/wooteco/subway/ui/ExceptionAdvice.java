package wooteco.subway.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.exception.SubwayMapException;

@RestControllerAdvice
public class ExceptionAdvice {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "서버가 요청을 처리할 수 없습니다.";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(e));
    }

    @ExceptionHandler(SubwayMapException.class)
    public ResponseEntity<ErrorResponse> handleSubWayMapException(final SubwayMapException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponse.from(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(final Exception e) {
        logger.error(
                "{}",
                e.getMessage(),
                e
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(INTERNAL_SERVER_ERROR_MESSAGE));
    }
}
