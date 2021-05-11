package wooteco.subway.exception;

public class IllegalUserInputException extends RuntimeException {

    private static final String MESSAGE = "[ERROR] 잘못된 입력입니다.";

    public IllegalUserInputException() {
        super(MESSAGE);
    }
}
