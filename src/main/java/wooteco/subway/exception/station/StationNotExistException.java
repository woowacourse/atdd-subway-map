package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;

public class StationNotExistException extends StationException {

    public StationNotExistException() {
        super(HttpStatus.BAD_REQUEST, "해당 역이 존재하지 않습니다.");
    }
}
