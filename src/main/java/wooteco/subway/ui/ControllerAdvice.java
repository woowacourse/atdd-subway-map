package wooteco.subway.ui;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.utils.exception.SubwayException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ErrorResponse> subwayExceptionHandler(SubwayException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> dataAccessExceptionHandler(DataAccessException e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(new ErrorResponse("[ERROR] 데이터를 조회할 수 없습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> ExceptionHandler(Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(new ErrorResponse("[ERROR] 데이터를 조회할 수 없습니다."));
    }
}
