package wooteco.subway.exception.line;

import wooteco.subway.exception.InvalidRequestException;

public class LineInsufficientRequestException extends InvalidRequestException {
    private static final String MESSAGE = "필수값이 잘못 되었습니다.";

    public LineInsufficientRequestException() {
        super(MESSAGE);
    }
}
