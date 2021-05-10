package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class NotEnoughSectionException extends SubwayException {

    private static final String MESSAGE = "최소 구간의 개수가 부족합니다.";

    public NotEnoughSectionException() {
        super(MESSAGE);
    }
}
