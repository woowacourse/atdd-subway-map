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
    public ResponseEntity<ExceptionMessageDto> duplicatedException(final SubwayException subwayException) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionMessageDto(subwayException.getMessage()));
    }
}
