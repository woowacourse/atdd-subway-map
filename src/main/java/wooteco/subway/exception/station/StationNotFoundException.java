package wooteco.subway.exception.station;

import wooteco.subway.exception.SubwayNotFoundException;

public class StationNotFoundException extends SubwayNotFoundException {

    private static final String MESSAGE = "해당 역이 존재하지 않습니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
