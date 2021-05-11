package wooteco.subway.exception;

public class DuplicateStationException extends DuplicateException {

    private static final String MESSAGE = "중복되는 역이 존재합니다.";

    public DuplicateStationException() {
        super(MESSAGE);
    }
}
