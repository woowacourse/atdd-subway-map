package wooteco.subway.exception;

public class NullColorException extends NullException {

    public NullColorException() {
        super("색상 값이 입력되지 않았습니다.");
    }
}
