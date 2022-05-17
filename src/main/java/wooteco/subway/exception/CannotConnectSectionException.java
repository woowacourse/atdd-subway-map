package wooteco.subway.exception;

public class CannotConnectSectionException extends IllegalArgumentException {

    public CannotConnectSectionException() {
        super("구간을 연결할 수 없습니다.");
    }
}
