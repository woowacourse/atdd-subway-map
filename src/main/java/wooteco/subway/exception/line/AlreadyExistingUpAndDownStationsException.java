package wooteco.subway.exception.line;

public class AlreadyExistingUpAndDownStationsException extends SubwayLineException {
    private static final String MESSAGE = "상행선과 하행선이 이미 존재합니다";

    public AlreadyExistingUpAndDownStationsException() {
        super(MESSAGE);
    }

}
