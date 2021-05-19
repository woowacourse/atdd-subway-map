package wooteco.subway.exception.line;

public class NegativeIdException extends SubwayLineException {
    private static final String MESSAGE = "id값은 음수일 수 없습니다.";

    public NegativeIdException() {
        super(MESSAGE);
    }

}
