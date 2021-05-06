package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.WebException;

public class LineException extends WebException {
    public LineException(HttpStatus httpStatus, Object body) {
        super(httpStatus, body);
    }
}
