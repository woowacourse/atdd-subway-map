package wooteco.subway.admin.domain.exception;

public class NoSuchLineException extends SubwayException {
    public NoSuchLineException() {
        super("존재하지 않는 노선입니다.");
    }
}
