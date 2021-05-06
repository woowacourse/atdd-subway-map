package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.WebException;

public class StationException extends WebException {

    public StationException(HttpStatus httpStatus, Object body) {
        super(httpStatus, body);
    }
}
