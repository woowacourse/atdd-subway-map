package wooteco.subway.line.exception;

public class LineNotFoundException extends LineException {
    private final static String message = "노선을 찾을 수 없습니다.";

    public LineNotFoundException() {
        super(message);
    }
}
