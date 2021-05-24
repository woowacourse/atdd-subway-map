package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidRequestException;

public class SectionLastRemainedException extends InvalidRequestException {

    private static final String MESSAGE = "최소 구간의 개수가 부족합니다.";

    public SectionLastRemainedException() {
        super(MESSAGE);
    }
}
