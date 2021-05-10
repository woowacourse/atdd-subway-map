package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class InsufficientLineInformationException extends SubwayException {
    private static final String MESSAGE = "필수값이 잘못 되었습니다.";

    public InsufficientLineInformationException() {
        super(MESSAGE);
    }
}
