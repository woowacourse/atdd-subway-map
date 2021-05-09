package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class StationDuplicationException extends SubwayException {
    public StationDuplicationException() {
        super(HttpStatus.BAD_REQUEST, "이미 존재하는 역입니다.");
    }
}
