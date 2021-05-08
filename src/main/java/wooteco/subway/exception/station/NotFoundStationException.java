package wooteco.subway.exception.station;

import wooteco.subway.exception.SubwayException;

public class NotFoundStationException extends SubwayException {

    public NotFoundStationException() {
        super("해당 역이 존재하지 않습니다.");
    }
}
