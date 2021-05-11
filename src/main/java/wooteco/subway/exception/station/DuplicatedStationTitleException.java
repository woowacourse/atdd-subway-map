package wooteco.subway.exception.station;

import wooteco.subway.exception.SubwayException;

public class DuplicatedStationTitleException extends SubwayException {
    public DuplicatedStationTitleException() {
        super("해당 역은 이미 존재합니다.");
    }
}
