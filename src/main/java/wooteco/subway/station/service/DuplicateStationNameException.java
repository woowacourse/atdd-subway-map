package wooteco.subway.station.service;

public class DuplicateStationNameException extends StationException {
    private static final String MESSAGE = "중복된 역 이름입니다.";

    public DuplicateStationNameException() {
        super(MESSAGE);
    }
}
