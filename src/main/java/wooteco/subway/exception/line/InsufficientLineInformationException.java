package wooteco.subway.exception.line;

public class InsufficientLineInformationException extends LineException {
    private static final String MESSAGE = "필수값이 잘못 되었습니다.";

    public InsufficientLineInformationException() {
        super(MESSAGE);
    }
}
