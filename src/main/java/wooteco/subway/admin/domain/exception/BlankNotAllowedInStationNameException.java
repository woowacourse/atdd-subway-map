package wooteco.subway.admin.domain.exception;

public class BlankNotAllowedInStationNameException extends SubwayException {
    public BlankNotAllowedInStationNameException() {
        super("역 이름에는 공백이 포함될 수 없습니다.");
    }
}
