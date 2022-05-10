package wooteco.subway.exception.constant;

public class BlankArgumentException extends IllegalArgumentException {

    private static final String MESSAGE = "값이 비어있을 수 없습니다.";
    ;

    public BlankArgumentException() {
        super(MESSAGE);
    }
}
