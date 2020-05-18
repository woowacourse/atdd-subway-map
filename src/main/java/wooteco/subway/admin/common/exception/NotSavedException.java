package wooteco.subway.admin.common.exception;

public class NotSavedException extends RuntimeException {
    private static final String FORMAT = "%s 값이 존재하지 않습니다.";

    public NotSavedException(final String s) {
        super(String.format(FORMAT, s));
    }
}
