package wooteco.subway.admin.domain.exception;

public class NoSuchEdgeException extends SubwayException {
    public NoSuchEdgeException() {
        super("존재하지 않는 구간입니다.");
    }
}
