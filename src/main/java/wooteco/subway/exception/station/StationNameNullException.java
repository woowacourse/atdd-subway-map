package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;

public class StationNameNullException extends StationException {
    public StationNameNullException() {
        super(HttpStatus.BAD_REQUEST, "역 이름은 필수입니다.");
    }
}
