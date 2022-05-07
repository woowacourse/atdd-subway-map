package wooteco.subway.exception.station;

import wooteco.subway.exception.NoSuchRecordException;

public class NoSuchStationException extends NoSuchRecordException {

    private static final String MESSAGE = "존재하지 않는 역입니다.";

    public NoSuchStationException() {
        super(MESSAGE);
    }
}
