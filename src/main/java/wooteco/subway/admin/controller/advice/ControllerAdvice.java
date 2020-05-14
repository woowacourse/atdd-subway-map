package wooteco.subway.admin.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wooteco.subway.admin.controller.DefinedSqlException;
import wooteco.subway.admin.domain.line.relation.InvalidLineStationException;
import wooteco.subway.admin.domain.line.vo.InvalidLineTimeTableException;
import wooteco.subway.admin.dto.SubwayErrorMessage;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({InvalidLineTimeTableException.class, InvalidLineStationException.class,
        IllegalArgumentException.class})
    public ResponseEntity<SubwayErrorMessage> getException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new SubwayErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(DefinedSqlException.class)
    public ResponseEntity<SubwayErrorMessage> getDefinedSQLException(DefinedSqlException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new SubwayErrorMessage(e.getMessage()));
    }
}
