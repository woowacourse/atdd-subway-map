package wooteco.subway.exception;

public class NullInputException extends SubwayException {

    public NullInputException() {
        super("null이 입력되었습니다.");
    }
}
