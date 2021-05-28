package wooteco.subway.exception;

public class SameStationSectionException extends NotAddableSectionException {

    private static final String SAME_STATION_SECTION_EXCEPTION = "동일한 역 사이의 구간은 생성할 수 없습니다.";

    public SameStationSectionException() {
        super(SAME_STATION_SECTION_EXCEPTION);
    }
}
