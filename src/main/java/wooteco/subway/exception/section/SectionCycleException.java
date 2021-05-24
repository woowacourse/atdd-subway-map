package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidRequestException;

public class SectionCycleException extends InvalidRequestException {

    private static final String MESSAGE = "사이클이 생기는 구간입니다.";

    public SectionCycleException() {
        super(MESSAGE);
    }
}
