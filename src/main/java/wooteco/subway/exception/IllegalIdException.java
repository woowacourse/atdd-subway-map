package wooteco.subway.exception;

public class IllegalIdException extends SubwayException {

    public IllegalIdException() {
        super("유효하지 않은 아이디입니다.");
    }
}
