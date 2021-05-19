package wooteco.subway.exception.line;

public class NegativeOrZeroDistanceException extends SubwayLineException {
    private static final String MESSAGE = "거리는 0이거나 음수일 수 없습니다.";

    public NegativeOrZeroDistanceException() {
        super(MESSAGE);
    }
    
}
