package wooteco.subway.admin.domain.exception;

public class RequireLineNameException extends SubwayException {
    public RequireLineNameException() {
        super("노선의 이름을 입력해주세요.");
    }
}
