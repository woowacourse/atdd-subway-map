package wooteco.subway.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorMessageResponse;
import wooteco.subway.exception.AccessNoneDataException;
import wooteco.subway.exception.DataLengthException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(DataLengthException.class)
    public ResponseEntity<ErrorMessageResponse> handleDataLengthException(DataLengthException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse(e.getMessage()));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorMessageResponse> handleEmptyResultDataAccessException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse("요청한 리소스를 DB에서 찾을 수 없습니다."));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorMessageResponse> handleDuplicateKeyException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse("중복된 데이터가 존재합니다."));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorMessageResponse> handleDataAccessException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse("DB관련 작업에서 오류가 발생했습니다."));
    }

    @ExceptionHandler(AccessNoneDataException.class)
    public ResponseEntity<ErrorMessageResponse> handleAccessNoneDataException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse("접근하려는 데이터가 존재하지 않습니다."));
    }
}
