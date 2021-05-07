package wooteco.subway.exception;

public class InsufficientLineInformationException extends RuntimeException{
    private static final String MESSAGE = "필수값이 잘못 되었습니다.";

    public InsufficientLineInformationException() {
        super(MESSAGE);
    }
}
