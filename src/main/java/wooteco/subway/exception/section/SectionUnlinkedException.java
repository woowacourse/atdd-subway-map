package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class SectionUnlinkedException extends SubwayException {

    private static final String MESSAGE = "연결되어 있는 구간이 없습니다.";

    public SectionUnlinkedException() {
        super(MESSAGE);
    }
}
