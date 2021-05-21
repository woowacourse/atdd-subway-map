package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class SectionAdditionException extends SubwayException {
    public SectionAdditionException() {
        super("추가할 수 없는 구간입니다.");
    }
}
