package wooteco.subway.exception;

public class NullIdException extends SubwayException {

    public NullIdException() {
        super("아이디 값이 입력되지 않았습니다.");
    }
}
