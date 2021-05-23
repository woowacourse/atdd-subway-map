package wooteco.subway.exception;

public class NotContainStationsException extends RuntimeException {

    private static final String MESSAGE = "노선에 포함되는 역이 없습니다.";

    public NotContainStationsException() {
        super(MESSAGE);
    }

}
