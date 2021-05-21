package wooteco.subway.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.exception.SectionException;
import wooteco.subway.station.exception.StationException;

@ControllerAdvice
public class BaseControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StationException.class)
    public ResponseEntity<ErrorResponse> stationExceptionHandling(Exception exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("STATION_EXCEPTION", exception.getMessage()));
    }

    @ExceptionHandler(LineException.class)
    public ResponseEntity<ErrorResponse> lineExceptionHandling(Exception exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("LINE_EXCEPTION", exception.getMessage()));
    }

    @ExceptionHandler(SectionException.class)
    public ResponseEntity<ErrorResponse> sectionExceptionHandling(Exception exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("SECTION_EXCEPTION", exception.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_FAILED", ex.getMessage()));
    }
}
