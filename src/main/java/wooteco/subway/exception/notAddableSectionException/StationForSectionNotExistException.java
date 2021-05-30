package wooteco.subway.exception.notAddableSectionException;

public class StationForSectionNotExistException extends NotAddableSectionException {

    private static final String STATION_FOR_SECTION_NOT_EXIST = "하나 이상의 역이 구간에 등록되어 있어야 합니다.";

    public StationForSectionNotExistException() {
        super(STATION_FOR_SECTION_NOT_EXIST);
    }
}
