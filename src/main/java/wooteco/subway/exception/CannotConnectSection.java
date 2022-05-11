package wooteco.subway.exception;

public class CannotConnectSection extends IllegalArgumentException {

    public CannotConnectSection() {
        super("구간을 연결할 수 없습니다.");
    }
}
