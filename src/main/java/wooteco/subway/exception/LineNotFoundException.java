package wooteco.subway.exception;

public class LineNotFoundException extends SubwayException {
    private static final String MESSAGE = "존재하지 않는 지하철 노선 입니다.";

    public LineNotFoundException() {
        super(MESSAGE);
    }

    public LineNotFoundException(final String message) {
        super(message);
    }

    public LineNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
