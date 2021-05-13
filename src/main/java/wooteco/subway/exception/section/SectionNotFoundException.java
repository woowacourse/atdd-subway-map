package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayNotFoundException;

public class SectionNotFoundException extends SubwayNotFoundException {

    private static final String MESSAGE = "구간이 존재하지 않습니다.";

    public SectionNotFoundException() {
        super(MESSAGE);
    }
}
