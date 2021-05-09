package wooteco.subway.exception;

public class StationNotFoundException extends SubwayException {

    private static final String MESSAGE = "역을 찾을 수 없습니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
