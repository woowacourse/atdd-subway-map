package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class SectionDuplicationException extends SubwayException {
    public SectionDuplicationException() {
        super("상행역과 하행역은 중복되지 않게 입력해주세요.");
    }
}
