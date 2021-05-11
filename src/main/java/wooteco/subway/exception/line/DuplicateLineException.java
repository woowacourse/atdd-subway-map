package wooteco.subway.exception.line;

public class DuplicateLineException extends SubwayLineException {
    private static final String MESSAGE = "동알한 라인은 등록할 수 없습니다.";

    public DuplicateLineException() {
        super(MESSAGE);
    }

}
