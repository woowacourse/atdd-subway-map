package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class StationException extends SubwayException {

    public StationException(HttpStatus httpStatus, Object body) {
        super(httpStatus, body);
    }
}
