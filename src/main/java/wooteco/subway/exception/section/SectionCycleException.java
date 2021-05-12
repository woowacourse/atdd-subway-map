package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class SectionCycleException extends SubwayException {

    private static final String MESSAGE = "사이클이 생기는 구간입니다.";

    public SectionCycleException() {
        super(MESSAGE);
    }
}
