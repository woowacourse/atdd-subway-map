package wooteco.subway.exception;

public class LineNotFoundException extends SubwayException {

    public LineNotFoundException(String message) {
        super(message);
    }

    public LineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
