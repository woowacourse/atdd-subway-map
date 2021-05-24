package wooteco.subway.exception.station;

import wooteco.subway.exception.NotFoundException;

public class StationNotFoundException extends NotFoundException {

    private static final String MESSAGE = "해당 역이 존재하지 않습니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
