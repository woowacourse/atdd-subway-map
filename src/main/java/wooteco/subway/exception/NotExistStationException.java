package wooteco.subway.exception;

public class NotExistStationException extends NotExistItemException {

    private static final String MESSAGE = "역이 존재하지 않습니다.";

    public NotExistStationException() {
        super(MESSAGE);
    }
}
