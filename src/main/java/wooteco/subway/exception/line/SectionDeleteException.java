package wooteco.subway.exception.line;

public class SectionDeleteException extends SubwayLineException {
    private static final String MESSAGE = "구간이 하나인 노선은 구간을 제거할 수 없습니다.";

    public SectionDeleteException() {
        super(MESSAGE);
    }
}
