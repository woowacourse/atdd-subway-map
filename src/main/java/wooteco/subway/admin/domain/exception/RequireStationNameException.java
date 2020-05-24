package wooteco.subway.admin.domain.exception;

public class RequireStationNameException extends SubwayException {
    public RequireStationNameException() {
        super("역의 이름을 입력해주세요.");
    }
}
