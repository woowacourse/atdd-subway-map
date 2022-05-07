package wooteco.subway.exception;

public class GlobalUnknownException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "확인되지 않은 예외가 발생했습니다. 관리자에게 문의해주세요.";

    public GlobalUnknownException() {
        super(DEFAULT_MESSAGE);
    }
}
