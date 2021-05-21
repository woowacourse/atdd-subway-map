package wooteco.subway.exception.section;

import wooteco.subway.exception.SubwayException;

public class SectionDeletionException extends SubwayException {
    public SectionDeletionException() {
        super("삭제할 수 없는 구간입니다.");
    }
}
