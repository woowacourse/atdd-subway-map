package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class InvalidDistanceException extends SubwayException {
    public InvalidDistanceException() {
        super("거리가 0 이하입니다.");
    }
}
