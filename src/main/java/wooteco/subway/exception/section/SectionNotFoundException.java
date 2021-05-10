package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class SectionNotFoundException extends SubwayException {

    private static final String MESSAGE = "구간이 존재하지 않습니다.";

    public SectionNotFoundException() {
        super(MESSAGE);
    }
}
