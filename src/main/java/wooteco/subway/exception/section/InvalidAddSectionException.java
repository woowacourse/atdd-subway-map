package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class InvalidAddSectionException extends SubwayException {
    public InvalidAddSectionException() {
        super("추가할 수 없는 구간입니다.");
    }
}
