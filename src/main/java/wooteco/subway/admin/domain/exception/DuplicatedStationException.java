package wooteco.subway.admin.domain.exception;

public class DuplicatedStationException extends SubwayException {
    public DuplicatedStationException() {
        super("이미 중복된 역이 존재합니다.");
    }
}
