package wooteco.subway.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorMessageResponse;
import wooteco.subway.exception.DataLengthException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(DataLengthException.class)
    public ResponseEntity<ErrorMessageResponse> DataLengthException(DataLengthException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(e.getMessage()));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorMessageResponse> EmptyResultDataAccessException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse("요청한 리소스를 DB에서 찾을 수 없습니다."));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorMessageResponse> DuplicateKeyException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse("중복된 데이터가 존재합니다."));
    }
}
