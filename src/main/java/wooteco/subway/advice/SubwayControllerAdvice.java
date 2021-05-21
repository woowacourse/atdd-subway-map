package wooteco.subway.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.advice.dto.ExceptionMessageDto;
import wooteco.subway.exception.badrequest.BadRequest;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<ExceptionMessageDto> subwayException(final BadRequest badRequest) {
        return ResponseEntity
                .badRequest()
                .body(new ExceptionMessageDto(badRequest.getMessage()));
    }
}
