package wooteco.subway.station.repository;

public class NoSuchStationException extends StationRepositoryException {
    private static final String MESSAGE = "존재하지 않는 역 입니다.";

    public NoSuchStationException() {
        super(MESSAGE);
    }
}
