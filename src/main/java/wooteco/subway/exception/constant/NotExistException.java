package wooteco.subway.exception.constant;

public class NotExistException extends IllegalArgumentException {

    private static final String MESSAGE = "존재하지 않습니다.";

    public NotExistException() {
        super(MESSAGE);
    }
}
