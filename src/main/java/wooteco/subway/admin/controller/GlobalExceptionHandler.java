package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.admin.dto.ErrorResponse;
import wooteco.subway.admin.exception.DuplicatedLineException;
import wooteco.subway.admin.exception.DuplicatedLineStationException;
import wooteco.subway.admin.exception.DuplicatedStationException;
import wooteco.subway.admin.exception.InvalidStationNameException;
import wooteco.subway.admin.exception.NotFoundLineException;
import wooteco.subway.admin.exception.NotFoundLineStationException;
import wooteco.subway.admin.exception.NotFoundStationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class, InvalidStationNameException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(Exception e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("유효한 요청이 아닙니다."));
    }

    @ExceptionHandler(NotFoundStationException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundStationException(
            NotFoundStationException e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("역이 존재하지 않습니다."));
    }

    @ExceptionHandler(NotFoundLineException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundLineException(NotFoundLineException e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("노선이 존재하지 않습니다."));
    }

    @ExceptionHandler(NotFoundLineStationException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundLineStationException(
            NotFoundLineStationException e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("구간이 존재하지 않습니다."));
    }

    @ExceptionHandler(DuplicatedStationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedStationException(
            DuplicatedStationException e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("이미 존재하는 역을 추가할 수 없습니다."));
    }

    @ExceptionHandler(DuplicatedLineException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedLineException(DuplicatedLineException e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("이미 존재하는 노선을 추가할 수 없습니다."));
    }

    @ExceptionHandler(DuplicatedLineStationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedLineStationException(
            DuplicatedLineStationException e) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("이미 존재하는 구간을 추가할 수 없습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("서버에서 오류가 발생하였습니다."));
    }
}
