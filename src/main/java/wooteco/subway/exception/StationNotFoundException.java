package wooteco.subway.exception;

public class StationNotFoundException extends RuntimeException {

    private static final String MESSAGE = "해당 역이 존재하지 않습니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
