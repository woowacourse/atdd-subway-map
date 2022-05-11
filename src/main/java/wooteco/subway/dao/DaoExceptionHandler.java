package wooteco.subway.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.ErrorMessageResponse;

@ControllerAdvice
public class DaoExceptionHandler {

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorMessageResponse> handleEmptyResultDataAccessException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse("요청한 리소스를 찾을 수 없습니다."));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorMessageResponse> handleDuplicateKeyException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse("중복된 데이터가 존재합니다."));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorMessageResponse> handleDataAccessException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse("데이터 관련 작업중 오류가 발생했습니다."));
    }
}
