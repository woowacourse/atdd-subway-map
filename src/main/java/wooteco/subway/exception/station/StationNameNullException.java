package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;

public class StationNameNullException extends StationException {
    public StationNameNullException() {
        super(HttpStatus.BAD_REQUEST, "노선 이름은 필수로 입력 해야 합니다.");
    }
}
