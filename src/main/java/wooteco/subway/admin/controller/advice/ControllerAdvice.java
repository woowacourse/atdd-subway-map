package wooteco.subway.admin.controller.advice;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.domain.line.relation.InvalidLineStationException;
import wooteco.subway.admin.domain.line.vo.InvalidLineTimeTableException;
import wooteco.subway.admin.dto.SubwayErrorMessage;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({InvalidLineTimeTableException.class, InvalidLineStationException.class,
        SQLException.class, NoSuchElementException.class})
    public ResponseEntity<SubwayErrorMessage> getException(Exception e) {
        return ResponseEntity.badRequest().body(new SubwayErrorMessage(e.getMessage()));
    }
}
