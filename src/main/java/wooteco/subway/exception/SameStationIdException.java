package wooteco.subway.exception;

public class SameStationIdException extends SectionException {

    public SameStationIdException() {
        super("상행역과 하행역은 다른 역이어야 합니다.");
    }
}
