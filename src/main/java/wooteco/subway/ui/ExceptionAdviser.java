package wooteco.subway.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.dto.response.ExceptionResponse;
import wooteco.subway.exception.CustomException;

@RestControllerAdvice
public class ExceptionAdviser {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handle(CustomException exception) {
        logger.info(exception.getClass().getName() + " -> " + exception.getMessage());
        return ResponseEntity.badRequest().body(ExceptionResponse.of(exception));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handle(RuntimeException exception) {
        logger.warn(exception.getClass().getName() + " -> " + exception.getMessage());
        return ResponseEntity.internalServerError().body(ExceptionResponse.of(exception));
    }

}
