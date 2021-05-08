package wooteco.subway.station.service;

public class NoSuchStationException extends StationException {
    private static final String MESSAGE = "존재하지 않는 역 입니다.";

    public NoSuchStationException() {
        super(MESSAGE);
    }
}
