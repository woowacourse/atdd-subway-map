package wooteco.subway.exception.line;

public class LineNotFoundException extends LineException {
    private static final String MESSAGE = "해당하는 라인이 존재히지 않습니다.";

    public LineNotFoundException() {
        super(MESSAGE);
    }
}
