package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class InvalidDistanceException extends SubwayException {

    private static final String MESSAGE = "새로 추가할 거리가 기존 거리보다 같거나 큽니다.";

    public InvalidDistanceException() {
        super(MESSAGE);
    }
}
