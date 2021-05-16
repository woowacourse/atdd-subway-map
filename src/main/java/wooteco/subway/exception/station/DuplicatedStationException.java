package wooteco.subway.exception.station;

public class DuplicatedStationException extends StationException {

    private static final String MESSAGE = "이미 존재하는 역 이름 입니다.";

    public DuplicatedStationException() {
        super(MESSAGE);
    }
}
