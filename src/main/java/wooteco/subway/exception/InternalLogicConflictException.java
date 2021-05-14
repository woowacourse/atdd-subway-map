package wooteco.subway.exception;

public class InternalLogicConflictException extends RuntimeException {
    private static final String MESSAGE = "내부 로직에 이상이 있습니다.";

    public InternalLogicConflictException() {
        super(MESSAGE);
    }
}
