package wooteco.subway.exception.station;

import wooteco.subway.exception.SubwayException;

public class StationNotFoundException extends SubwayException {
    public StationNotFoundException() {
        super("존재하지 않는 역입니다.");
    }
}
