package wooteco.subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorMessageResponse;
import wooteco.subway.exception.BadRequestLineException;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> notFountException(NotFoundException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(e.getMessage()));
    }

    @ExceptionHandler({BadRequestLineException.class, IllegalArgumentException.class, IllegalStateException.class,
            DuplicateNameException.class})
    public ResponseEntity<ErrorMessageResponse> badRequestLineException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(e.getMessage()));
    }
}
