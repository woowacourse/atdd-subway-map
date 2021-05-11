package wooteco.subway.exception.illegal;

public class ImpossibleDistanceException extends IllegalMethodException {

    private static final String MESSAGE = "불가능한 거리입니다.";

    public ImpossibleDistanceException() {
        super(MESSAGE);
    }
}
