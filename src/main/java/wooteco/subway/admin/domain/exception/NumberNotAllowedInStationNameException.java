package wooteco.subway.admin.domain.exception;

public class NumberNotAllowedInStationNameException extends SubwayException {
    public NumberNotAllowedInStationNameException() {
        super("역 이름에는 숫자가 포함될 수 없습니다.");
    }
}
