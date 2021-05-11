package wooteco.subway.exception.nosuch;

public class NoSuchStationException extends NoSuchException {

    private static final String MESSAGE = "역이 존재하지 않습니다.";

    public NoSuchStationException() {
        super(MESSAGE);
    }
}
