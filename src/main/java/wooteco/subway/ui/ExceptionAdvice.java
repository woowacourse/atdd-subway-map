package wooteco.subway.ui;

import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.exception.SubwayMapException;

@RestControllerAdvice
public class ExceptionAdvice {

    private static final String INVALID_REQUEST_FORMAT = "요청 값 형식이 올바르지 않습니다.";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "서버가 요청을 처리할 수 없습니다.";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(final MethodArgumentNotValidException e) {
        final Map<String, String> body = e.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        it -> ((FieldError) it).getField(),
                        DefaultMessageSourceResolvable::getDefaultMessage
                ));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageException(final HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(INVALID_REQUEST_FORMAT));
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
