package wooteco.subway.exception;

public class ImpossibleDistanceException extends IllegalArgumentException {

    private static final String MESSAGE = "불가능한 거리입니다.";

    public ImpossibleDistanceException() {
        super(MESSAGE);
    }
}
