package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidRequestException;

public class SectionUnlinkedException extends InvalidRequestException {

    private static final String MESSAGE = "연결되어 있는 구간이 없습니다.";

    public SectionUnlinkedException() {
        super(MESSAGE);
    }
}
