package wooteco.subway.exception.station;

import wooteco.subway.exception.SubwayException;

public class DuplicateStationException extends SubwayException {

    public DuplicateStationException() {
        super("중복된 역이 존재합니다.");
    }
}
