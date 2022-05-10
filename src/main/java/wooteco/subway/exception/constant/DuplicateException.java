package wooteco.subway.exception.constant;

public class DuplicateException extends IllegalArgumentException {

    private static final String MESSAGE = "중복을 허용하지 않습니다.";

    public DuplicateException() {
        super(MESSAGE);
    }
}
