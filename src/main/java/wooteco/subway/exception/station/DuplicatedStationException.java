package wooteco.subway.exception.station;

import wooteco.subway.exception.CustomException;

public class DuplicatedStationException extends CustomException {

    private static final String MESSAGE = "이미 존재하는 역 이름입니다.";

    public DuplicatedStationException() {
        super(MESSAGE);
    }
}
