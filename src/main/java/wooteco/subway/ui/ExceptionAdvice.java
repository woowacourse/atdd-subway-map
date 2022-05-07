package wooteco.subway.ui;

import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.exception.SubwayMapException;

@ControllerAdvice(assignableTypes = {StationController.class, LineController.class})
public class ExceptionAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(e));
    }

    @ExceptionHandler(value = {SubwayMapException.class})
    public ResponseEntity<ErrorResponse> handleSubWayMapException(final SubwayMapException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponse.from(e));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponse> handleUnexpectedException(final Exception e,
                                                                   final HttpServletRequest request) {
        log(e, request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from("서버가 요청을 처리할 수 없습니다."));
    }

    private void log(final Exception e, final HttpServletRequest request) {
        final String messageFormat = "[{}] {} {}";
        logger.error(
                messageFormat,
                LocalDateTime.now(),
                request.getMethod(),
                request.getRequestURI(),
                e
        );
    }
}
