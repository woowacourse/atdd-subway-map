package wooteco.subway.aop.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.line.exception.Line4XXException;
import wooteco.subway.section.exception.Section4XXException;
import wooteco.subway.station.exception.Station4XXException;

@RestControllerAdvice({
        "wooteco.subway.line.controller",
        "wooteco.subway.station.controller",
        "wooteco.subway.section.controller"
})
public class ExceptionController {
    @ExceptionHandler({Section4XXException.class, Line4XXException.class, Station4XXException.class})
    public ResponseEntity<Void> exceptionHandler() {
        return ResponseEntity.badRequest().build();
    }
}