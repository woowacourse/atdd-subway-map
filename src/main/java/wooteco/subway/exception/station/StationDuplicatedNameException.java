package wooteco.subway.exception.station;

import wooteco.subway.exception.SubwayException;

public class StationDuplicatedNameException extends SubwayException {
    public StationDuplicatedNameException() {
        super("중복된 이름의 역이 존재합니다");
    }
}
