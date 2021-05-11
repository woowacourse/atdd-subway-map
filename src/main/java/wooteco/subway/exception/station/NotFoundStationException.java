package wooteco.subway.exception.station;

import wooteco.subway.exception.NotFoundException;

public class NotFoundStationException extends NotFoundException {

    public NotFoundStationException() {
        super("해당 역이 존재하지 않습니다.");
    }
}
