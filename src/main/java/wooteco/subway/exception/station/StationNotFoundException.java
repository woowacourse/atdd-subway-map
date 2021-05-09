package wooteco.subway.exception.station;

public class StationNotFoundException extends RuntimeException {

    private static final String MESSAGE = "등록되어 있는 역이 없습니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
