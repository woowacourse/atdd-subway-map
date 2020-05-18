package wooteco.subway.admin.domain.exception;

public class RequireFirstStationException extends SubwayException {
    public RequireFirstStationException() {
        super("시작역부터 입력해주세요.");
    }
}
