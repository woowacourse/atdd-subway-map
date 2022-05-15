package wooteco.subway.ui;

import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import wooteco.subway.dto.ErrorResponse;
import wooteco.subway.exception.DataNotExistException;
import wooteco.subway.exception.SubwayException;

@ControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(DataNotExistException.class)
    public ResponseEntity<ErrorResponse> handleIDataNotExistException() {
        ErrorResponse errorResponse = new ErrorResponse("질못된 요청입니다.");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ResponseEntity<ErrorResponse> handleSQLException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Void> handleNoHandlerFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException() {
        ErrorResponse errorResponse = new ErrorResponse("잘못된 접근입니다.");
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}
