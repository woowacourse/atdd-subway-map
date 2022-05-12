package wooteco.subway.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class SubwayControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpected(Exception e) {
        return new ResponseEntity<>("예상하지 못한 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handle(Exception e) {
        return new ResponseEntity<>("페이지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleInvalidArgument(Exception e) {
        return new ResponseEntity<>("이름이나 색깔이 공백입니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> handleEmptyResultData(Exception e) {
        return new ResponseEntity<>("해당 데이터를 조회 할 수 없습니다.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleBadRequest(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
