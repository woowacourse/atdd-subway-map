package wooteco.subway.exception.line;

public class NameLengthException extends SubwayLineException {
    private static final String MESSAGE = "이름은 0 보다 커야 합니다.";

    public NameLengthException() {
        super(MESSAGE);
    }

}
