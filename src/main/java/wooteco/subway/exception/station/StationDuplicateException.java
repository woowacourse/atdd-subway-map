package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;

public class StationDuplicateException extends StationException {
    public StationDuplicateException() {
        super(HttpStatus.BAD_REQUEST, "해당 이름의 역이 이미 존재합니다.");
    }
}
