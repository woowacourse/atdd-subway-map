package wooteco.subway.exception;

public class NotFoundTerminalStationException extends RuntimeException {

    private static final String MESSAGE = "상행 종점역을 찾을 수 업습니다.";

    public NotFoundTerminalStationException() {
        super(MESSAGE);
    }

}
