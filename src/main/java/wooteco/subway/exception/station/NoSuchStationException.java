package wooteco.subway.exception.station;

import wooteco.subway.exception.NoSuchContentException;

public class NoSuchStationException extends NoSuchContentException {
    private static final String MESSAGE = "존재하지 않는 역입니다.";

    public NoSuchStationException() {
        super(MESSAGE);
    }
}
