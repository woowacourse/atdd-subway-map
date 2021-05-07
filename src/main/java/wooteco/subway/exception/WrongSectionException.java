package wooteco.subway.exception;

public class WrongSectionException extends RuntimeException {

    private static final String MESSAGE = "잘못된 구간입니다.";

    public WrongSectionException() {
        super(MESSAGE);
    }
}
