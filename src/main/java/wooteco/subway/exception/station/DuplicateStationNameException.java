package wooteco.subway.exception.station;

import wooteco.subway.exception.DuplicateNameException;

public class DuplicateStationNameException extends DuplicateNameException {

    private static final String MESSAGE = "중복된 역 이름입니다.";

    public DuplicateStationNameException() {
        super(MESSAGE);
    }
}
