package wooteco.subway.exception;

public class DuplicatedSectionException extends DuplicateException {

    private static final String MESSAGE = "중복된 구간입니다.";

    public DuplicatedSectionException() {
        super(MESSAGE);
    }
}
