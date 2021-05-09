package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;

public class StationNotFoundException extends StationException {

    public StationNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "해당 역이 존재하지 않습니다.");
    }
}
