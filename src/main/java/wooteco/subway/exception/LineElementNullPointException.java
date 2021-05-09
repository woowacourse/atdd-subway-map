package wooteco.subway.exception;

public class LineElementNullPointException extends SubwayException {

    private static final String MESSAGE = "필수 요소가 누락 되었습니다.";

    public LineElementNullPointException() {
        super(MESSAGE);
    }
}
