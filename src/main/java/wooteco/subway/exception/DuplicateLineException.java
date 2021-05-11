package wooteco.subway.exception;

public class DuplicateLineException extends DuplicateException {
    private static final String MESSAGE = "중복되는 노선 이름이 존재합니다.";

    public DuplicateLineException() {
        super(MESSAGE);
    }
}
