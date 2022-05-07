package wooteco.subway.exception.station;

import wooteco.subway.exception.IllegalInputException;

public class DuplicateStationException extends IllegalInputException {

    private static final String MESSAGE = "역 정보가 중복됩니다.";

    public DuplicateStationException() {
        super(MESSAGE);
    }
}
