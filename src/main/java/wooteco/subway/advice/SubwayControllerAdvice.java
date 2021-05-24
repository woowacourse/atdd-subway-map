package wooteco.subway.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.advice.dto.ExceptionMessageDto;
import wooteco.subway.exception.SubwayException;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ExceptionMessageDto> subwayException(final SubwayException e) {
        return ResponseEntity
                .status(e.httpStatus())
                .body(new ExceptionMessageDto(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessageDto> exception(final Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionMessageDto("서버에서 요청을 처리하지 못했습니다."));
    }
}
