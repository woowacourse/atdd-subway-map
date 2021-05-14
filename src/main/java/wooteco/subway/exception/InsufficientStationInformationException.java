package wooteco.subway.exception;

public class InsufficientStationInformationException extends SubwayException {

    private static final String MESSAGE = "필수값이 잘못 되었습니다.";

    public InsufficientStationInformationException() {
        super(MESSAGE);
    }
}
