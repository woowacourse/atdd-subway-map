package wooteco.subway.admin.domain.exception;

public class NoSuchStationException extends SubwayException {
    public NoSuchStationException() {
        super("존재하지 않는 역입니다.");
    }
}
