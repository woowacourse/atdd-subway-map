package wooteco.subway.exception;

public class StationDuplicateException extends RuntimeException {
    public StationDuplicateException(final String msg) {
        super(msg);
    }
}
