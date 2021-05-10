package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class NotPositiveDistanceException extends SubwayException {

    private static final String MESSAGE = "자연수가 아닌 거리를 등록하셨습니다.";

    public NotPositiveDistanceException() {
        super(MESSAGE);
    }
}
