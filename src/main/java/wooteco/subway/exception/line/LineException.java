package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class LineException extends SubwayException {
    public LineException(HttpStatus httpStatus, Object body) {
        super(httpStatus, body);
    }
}
