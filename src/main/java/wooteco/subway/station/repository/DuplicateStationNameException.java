package wooteco.subway.station.repository;

public class DuplicateStationNameException extends RuntimeException {
    private static final String MESSAGE = "중복된 역 이름입니다.";

    public DuplicateStationNameException() {
        super(MESSAGE);
    }
}
