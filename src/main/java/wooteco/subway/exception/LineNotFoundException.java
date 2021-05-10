package wooteco.subway.exception;

public class LineNotFoundException extends SubwayException {

    private static final String MESSAGE = "노선을 찾을 수 없습니다.";

    public LineNotFoundException() {
        super(MESSAGE);
    }
}
