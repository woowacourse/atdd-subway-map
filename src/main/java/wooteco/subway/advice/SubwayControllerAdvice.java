package wooteco.subway.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.advice.dto.ExceptionResponse;
import wooteco.subway.exception.SubwayException;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ExceptionResponse> subwayException(final SubwayException e) {
        return ResponseEntity
                .status(e.httpStatus())
                .body(new ExceptionResponse(e.getMessage(), e.httpStatus().value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> exception(final Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("서버에서 요청을 처리하지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
