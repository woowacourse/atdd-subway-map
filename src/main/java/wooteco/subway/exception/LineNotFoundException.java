package wooteco.subway.exception;

public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException(final String msg) {
        super(msg);
    }
}
