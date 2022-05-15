package wooteco.subway.exception.notfound;

import wooteco.subway.exception.ExceptionMessage;

public class StationNotFoundException extends NotFoundException {
    public StationNotFoundException() {
        super(ExceptionMessage.NOT_FOUND_STATION.getContent());
    }
}
