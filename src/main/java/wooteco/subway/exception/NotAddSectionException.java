package wooteco.subway.exception;

public class NotAddSectionException extends RuntimeException {

    private static final String MESSAGE = "구간을 추가할 수 없습니다.";

    public NotAddSectionException() {
        super(MESSAGE);
    }

}
