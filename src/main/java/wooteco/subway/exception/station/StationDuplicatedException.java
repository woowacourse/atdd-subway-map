package wooteco.subway.exception.station;

import wooteco.subway.exception.InvalidRequestException;

public class StationDuplicatedException extends InvalidRequestException {

    private static final String MESSAGE = "이미 존재하는 역 이름 입니다.";

    public StationDuplicatedException() {
        super(MESSAGE);
    }
}
