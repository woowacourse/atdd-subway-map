package wooteco.subway.exception;

public class NullNameException extends NullException {

    public NullNameException() {
        super("이름 값이 입력되지 않았습니다.");
    }
}