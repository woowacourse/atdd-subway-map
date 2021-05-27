package wooteco.subway.exception;

public class SameStationSectionException extends RuntimeException {

    private static final String EQUAL_STATION_MESSAGE = "동일한 역 사이의 구간은 생성할 수 없습니다.";

    public SameStationSectionException() {
        super(EQUAL_STATION_MESSAGE);
    }
}
