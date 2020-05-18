package wooteco.subway.admin.common.exception;

public class AlreadySavedException extends RuntimeException {
    private static final String FORMAT = "%s : 이미 저장되어 있는 정보입니다.";

    public AlreadySavedException(final String s) {
        super(String.format(FORMAT, s));
    }
}
