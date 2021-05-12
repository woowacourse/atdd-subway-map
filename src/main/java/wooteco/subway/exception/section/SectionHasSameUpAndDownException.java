package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class SectionHasSameUpAndDownException extends SubwayException {

    private static final String MESSAGE = "상행,하행역이 같은 구간입니다.";

    public SectionHasSameUpAndDownException() {
        super(MESSAGE);
    }
}
