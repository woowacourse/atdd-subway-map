package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;

public class StationNamePatternException extends StationException {
    public StationNamePatternException() {
        super(HttpStatus.BAD_REQUEST, "올바르지 않은 역 이름입니다.");
    }
}
