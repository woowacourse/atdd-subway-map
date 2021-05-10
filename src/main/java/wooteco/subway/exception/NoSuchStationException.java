package wooteco.subway.exception;

public class NoSuchStationException extends IllegalArgumentException {

    private static final String MESSAGE = "역이 존재하지 않습니다.";

    public NoSuchStationException() {
        super(MESSAGE);
    }
}
