package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class IncludedStationException extends SubwayException {
    public IncludedStationException() {
        super("구간에 포함되어 있는 역 입니다.");
    }
}
