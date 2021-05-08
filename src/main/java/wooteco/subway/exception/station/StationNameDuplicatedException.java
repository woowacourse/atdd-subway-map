package wooteco.subway.exception.station;

import wooteco.subway.exception.SubwayException;

public class StationNameDuplicatedException extends SubwayException {

    private static final String MESSAGE = "중복된 역 이름입니다.";

    public StationNameDuplicatedException() {
        super(MESSAGE);
    }
}
