package wooteco.subway.exception;

public class BothStationInLineException extends IllegalMethodException {
    private static final String MESSAGE = "두 역이 이미 같은 노선에 존재합니다.";

    public BothStationInLineException() {
        super(MESSAGE);
    }
}
