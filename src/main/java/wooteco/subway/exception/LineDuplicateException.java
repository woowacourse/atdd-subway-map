package wooteco.subway.exception;

public class LineDuplicateException extends RuntimeException {
    public LineDuplicateException(final String msg) {
        super(msg);
    }
}
