package wooteco.subway.exception;

public class IllegalInputException extends IllegalArgumentException {

    private static final String MESSAGE = "입력이 올바르지 않습니다.";

    public IllegalInputException() {
        super(MESSAGE);
    }
}
